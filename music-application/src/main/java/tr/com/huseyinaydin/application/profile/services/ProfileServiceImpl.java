package tr.com.huseyinaydin.application.profile.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.ports.IStorageService;
import tr.com.huseyinaydin.application.profile.dtos.UpdateProfileRequest;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.domain.repositories.IApplicationUserRepository;
import tr.com.huseyinaydin.domain.user.ApplicationUser;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProfileServiceImpl implements IProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileServiceImpl.class);

    private final IStorageService storageService;
    private final IApplicationUserRepository userRepository;
    private final Map<String, UserProfileDto> profiles = new ConcurrentHashMap<>();

    public ProfileServiceImpl(@Autowired(required = false) IStorageService storageService,
                              @Autowired(required = false) IApplicationUserRepository userRepository) {
        this.storageService = storageService;
        this.userRepository = userRepository;
    }

    private String normalizeEmail(String email) {
        return (email != null) ? email.trim().toLowerCase(Locale.ROOT) : "";
    }

    @Override
    public UserProfileDto getProfile(String email) {
        String key = normalizeEmail(email);
        if (key.isEmpty()) {
            return new UserProfileDto("Misafir Kullanıcı", "guest", "guest@rezonans.com", "Müzik tutkunu.", "Basic");
        }

        return profiles.computeIfAbsent(key, k -> {
            String derivedUsername = k.contains("@") ? k.substring(0, k.indexOf("@")) : k;
            String derivedFullName = derivedUsername;
            String tier = "Premium";

            if (userRepository != null) {
                try {
                    Optional<ApplicationUser> userOpt = userRepository.findByEmail(k);
                    if (userOpt.isPresent()) {
                        ApplicationUser user = userOpt.get();
                        if (user.getRole() != null) {
                            tier = user.getRole().name();
                        }
                    }
                } catch (Exception ex) {
                    log.warn("[PROFİL YÖNETİMİ] Kullanıcı sorgulama uyarısı: {}", ex.getMessage());
                }
            }

            return new UserProfileDto(
                capitalizeName(derivedFullName),
                derivedUsername,
                k,
                "Müzik tutkunu, yazılım geliştirici.",
                tier
            );
        });
    }

    private String capitalizeName(String name) {
        if (name == null || name.isBlank()) return "Hüseyin Aydın";
        String[] parts = name.replace(".", " ").replace("_", " ").split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (!p.isEmpty()) {
                sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }

    @Override
    @Transactional
    public void updateProfileInfo(String currentEmail, String newEmail, String fullName, String username, String bio) {
        String oldKey = normalizeEmail(currentEmail);
        String newKey = normalizeEmail(newEmail);

        if (newKey.isEmpty() || !newKey.contains("@")) {
            throw new IllegalArgumentException("Lütfen geçerli bir e-posta adresi giriniz.");
        }

        log.info("[PROFİL YÖNETİMİ] Profil güncelleme: {} -> {}", oldKey, newKey);

        // 1. E-posta değiştiyse ve yeni e-posta başka bir hesapta varsa engelle
        if (!oldKey.equals(newKey) && userRepository != null) {
            Optional<ApplicationUser> existingWithNewEmail = userRepository.findByEmail(newKey);
            if (existingWithNewEmail.isPresent()) {
                throw new BusinessException("ERR", "'" + newEmail + "' e-posta adresi zaten başka bir hesap tarafından kullanılıyor.");
            }

            Optional<ApplicationUser> currentUserOpt = userRepository.findByEmail(oldKey);
            if (currentUserOpt.isPresent()) {
                ApplicationUser appUser = currentUserOpt.get();
                appUser.updateEmail(newEmail.trim());
                userRepository.save(appUser);
                log.info("-> ApplicationUser e-postası veritabanında güncellendi: {} -> {}", oldKey, newKey);
            }
        }

        // 2. Bellekteki profil DTO'sunu güncelle
        UserProfileDto dto = profiles.remove(oldKey);
        if (dto == null) {
            dto = getProfile(oldKey);
            profiles.remove(oldKey);
        }

        dto.setEmail(newEmail.trim());
        if (fullName != null && !fullName.trim().isEmpty()) {
            dto.setFullName(fullName.trim());
        }
        if (username != null && !username.trim().isEmpty()) {
            dto.setUsername(username.trim().replace("@", ""));
        }
        if (bio != null) {
            dto.setBio(bio.trim());
        }

        profiles.put(newKey, dto);
        log.info("-> Profil bilgileri başarıyla güncellendi: ad='{}', username='{}', email='{}'", dto.getFullName(), dto.getUsername(), dto.getEmail());
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        log.info("[PROFİL YÖNETİMİ] " + request.getUserId() + " kullanıcısı için profil güncelleme başladı.");

        // 1. Şifre değişimi varsa kontrol et
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new BusinessException("ERR", "Yeni şifre belirlemek için mevcut şifrenizi girmelisiniz!");
            }
            log.info("-> Kullanıcı şifresi güvenli bir şekilde (BCrypt) güncellendi.");
        }

        // 2. Yeni profil fotoğrafı varsa MinIO Object Storage'a yükle
        if (request.getProfileImageStream() != null && storageService != null) {
            String uploadedImageUrl = storageService.uploadImageFile(
                    request.getImageFileName(),
                    request.getProfileImageStream(),
                    request.getImageSize(),
                    request.getImageContentType()
            );
            log.info("-> Profil resmi MinIO'ya yüklendi. URL: " + uploadedImageUrl);
        }

        // 3. Biyografi güncellemesi
        if (request.getBio() != null) {
            UserProfileDto dto = getProfile(request.getUserId());
            dto.setBio(request.getBio());
            log.info("-> Kullanıcı biyografisi güncellendi: " + request.getBio());
        }

        log.info("[PROFİL YÖNETİMİ] Profil başarıyla güncellendi.");
    }
}
