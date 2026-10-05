package tr.com.huseyinaydin.application.auth.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.ILoginService;
import tr.com.huseyinaydin.application.ports.IPasswordHashService;
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

    private final IApplicationUserRepository userRepository;
    private final IPasswordHashService passwordHashService;

    public LoginServiceImpl(IApplicationUserRepository userRepository, IPasswordHashService passwordHashService) {
        this.userRepository = userRepository;
        this.passwordHashService = passwordHashService;
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseTokenDto authenticate(LoginRequestDto request) {
        System.out.println("[GİRİŞ SİSTEMİ] '" + request.getEmail() + "' için doğrulama isteği atılıyor...");
        
        if (request.getPassword() == null || request.getPassword().length() == 0) {
            throw new BusinessException("ERR", "E-posta veya şifre hatalı!");
        }

        Optional<ApplicationUser> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isEmpty()) {
            System.err.println("[GİRİŞ SİSTEMİ] Kullanıcı bulunamadı: " + request.getEmail());
            throw new BusinessException("ERR", "E-posta veya şifre hatalı!");
        }

        ApplicationUser user = existing.get();
        PasswordHash hash = new PasswordHash(user.getPasswordHash(), user.getPasswordSalt());
        
        boolean isMatch = passwordHashService.verifyHash(request.getPassword(), hash);
        if (!isMatch) {
            System.err.println("[GİRİŞ SİSTEMİ] Hatalı şifre denemesi!");
            throw new BusinessException("ERR", "E-posta veya şifre hatalı!");
        }

        System.out.println("-> Kullanıcı doğrulandı. JWT Access Token üretiliyor.");
        
        // TODO: Entegre JWT provider
        String mockJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + UUID.randomUUID().toString();
        String mockRefreshToken = UUID.randomUUID().toString();
        
        System.out.println("[GİRİŞ SİSTEMİ] Kullanıcı başarıyla sisteme bağlandı.");
        return new ResponseTokenDto(mockJwtToken, mockRefreshToken, 3600, user.getRole().name());
    }
}
