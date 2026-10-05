package tr.com.huseyinaydin.application.auth.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IRegisterService;
import tr.com.huseyinaydin.application.ports.IPasswordHashService;
import tr.com.huseyinaydin.application.ports.PasswordHash;
import tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.domain.repositories.IApplicationUserRepository;
import tr.com.huseyinaydin.domain.user.ApplicationUser;
import tr.com.huseyinaydin.domain.enums.UserRole;

import java.util.Optional;

@Service
public class RegisterServiceImpl implements IRegisterService {

    private final IApplicationUserRepository userRepository;
    private final IPasswordHashService passwordHashService;

    public RegisterServiceImpl(IApplicationUserRepository userRepository, IPasswordHashService passwordHashService) {
        this.userRepository = userRepository;
        this.passwordHashService = passwordHashService;
    }

    @Override
    @Transactional
    public void registerNewUser(RegisterRequestDto request) {
        System.out.println("[KAYIT SİSTEMİ] Yeni kullanıcı ('" + request.getEmail() + "') için süreç başlatıldı.");
        
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new BusinessException("ERR", "Şifreniz en az 6 karakter olmalıdır!");
        }

        Optional<ApplicationUser> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new BusinessException("ERR", "Bu e-posta adresi zaten kullanılıyor.");
        }

        PasswordHash hash = passwordHashService.createHash(request.getPassword());
        UserRole role = request.isArtistAccount() ? UserRole.OFFICER : UserRole.CUSTOMER; // Example role mapping
        
        ApplicationUser user = new ApplicationUser(
                null, // customerId (optional/null for now until profile is created)
                request.getEmail(),
                hash.hash(),
                hash.salt(),
                role
        );
        
        userRepository.save(user);
        
        System.out.println("-> Yerel PostgreSQL veritabanında 'AppUser' varlığı başarıyla oluşturuldu.");
    }
}
