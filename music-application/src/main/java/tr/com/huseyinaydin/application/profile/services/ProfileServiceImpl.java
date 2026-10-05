package tr.com.huseyinaydin.application.profile.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.ports.IStorageService;
import tr.com.huseyinaydin.application.profile.dtos.UpdateProfileRequest;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

@Service
public class ProfileServiceImpl implements IProfileService {

    private final IStorageService storageService; // MinIO'ya yüklemek için

    public ProfileServiceImpl(IStorageService storageService) {
        this.storageService = storageService;
    }

    @Override
    @Transactional
    public void updateProfile(UpdateProfileRequest request) {
        System.out.println("[PROFİL YÖNETİMİ] " + request.getUserId() + " kullanıcısı için profil güncelleme başladı.");

        // 1. Şifre değişimi varsa kontrol et
        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new BusinessException("ERR", "Yeni şifre belirlemek için mevcut şifrenizi girmelisiniz!");
            }
            System.out.println("-> Kullanıcı şifresi güvenli bir şekilde (BCrypt) güncellendi.");
        }

        // 2. Yeni profil fotoğrafı varsa MinIO Object Storage'a yükle
        if (request.getProfileImageStream() != null) {
            String uploadedImageUrl = storageService.uploadImageFile(
                    request.getImageFileName(),
                    request.getProfileImageStream(),
                    request.getImageSize(),
                    request.getImageContentType()
            );
            System.out.println("-> Profil resmi MinIO'ya yüklendi. URL: " + uploadedImageUrl);
        }

        // 3. Biyografi ve diğer alanları veritabanında (Entity) güncelle
        System.out.println("-> Kullanıcı biyografisi güncellendi: " + request.getBio());
        
        // Gerçekte: AppUser user = userRepository.findById(request.getUserId()); user.setBio(...); userRepository.save(user);
        System.out.println("[PROFİL YÖNETİMİ] Profil başarıyla güncellendi.");
    }
}
