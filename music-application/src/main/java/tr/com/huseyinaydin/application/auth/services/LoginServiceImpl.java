package tr.com.huseyinaydin.application.auth.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.ILoginService;
import tr.com.huseyinaydin.application.ports.IPasswordHashService;
import tr.com.huseyinaydin.application.ports.ITokenService;
import org.springframework.beans.factory.annotation.Value;
import tr.com.huseyinaydin.application.ports.PasswordHash;
import tr.com.huseyinaydin.application.auth.dtos.LoginRequestDto;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.domain.repositories.IApplicationUserRepository;
import tr.com.huseyinaydin.domain.user.ApplicationUser;

import java.util.Optional;
import java.util.UUID;

@Service
public class LoginServiceImpl implements ILoginService {
    private static final Logger log = LoggerFactory.getLogger(LoginServiceImpl.class);


    private final IApplicationUserRepository userRepository;
    private final IPasswordHashService passwordHashService;

    private final ITokenService tokenService;
    private final String defaultTier;

    public LoginServiceImpl(IApplicationUserRepository userRepository,
                            IPasswordHashService passwordHashService,
                            ITokenService tokenService,
                            @Value("${music.membership.default-tier:Premium}") String defaultTier) {
        this.userRepository = userRepository;
        this.passwordHashService = passwordHashService;
        this.tokenService = tokenService;
        this.defaultTier = defaultTier;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseTokenDto authenticate(LoginRequestDto request) {
        log.info("[GİRİŞ SİSTEMİ] '" + request.getEmail() + "' için doğrulama isteği atılıyor...");
        
        if (request.getPassword() == null || request.getPassword().length() == 0) {
            throw new BusinessException("ERR", "E-posta veya şifre hatalı!");
        }

        Optional<ApplicationUser> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isEmpty()) {
            log.error("[GİRİŞ SİSTEMİ] Kullanıcı bulunamadı: " + request.getEmail());
            throw new BusinessException("ERR", "E-posta veya şifre hatalı!");
        }

        ApplicationUser user = existing.get();
        PasswordHash hash = new PasswordHash(user.getPasswordHash(), user.getPasswordSalt());
        
        boolean isMatch = passwordHashService.verifyHash(request.getPassword(), hash);
        if (!isMatch) {
            log.error("[GİRİŞ SİSTEMİ] Hatalı şifre denemesi!");
            throw new BusinessException("ERR", "E-posta veya şifre hatalı!");
        }

        log.info("-> Kullanıcı doğrulandı. JWT Access Token üretiliyor.");
        
        String accessToken = tokenService.generateAccessToken(user.getEmail(), user.getRole().name(), defaultTier);
        String mockRefreshToken = UUID.randomUUID().toString();
        
        log.info("[GİRİŞ SİSTEMİ] Kullanıcı başarıyla sisteme bağlandı.");
        return new ResponseTokenDto(accessToken, mockRefreshToken, tokenService.getExpirationSeconds(), user.getRole().name());
    }
}
