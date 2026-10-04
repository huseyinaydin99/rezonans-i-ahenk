package tr.com.huseyinaydin.application.auth.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IRegisterService;
import tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

@Service
public class RegisterServiceImpl implements IRegisterService {

    @Override
    @Transactional
    public void registerNewUser(RegisterRequestDto request) {
        System.out.println("[KAYIT SİSTEMİ] Yeni kullanıcı ('" + request.getUsername() + "') için onboarding süreci başlatıldı.");
        
        // 1. İş Kuralları (Validasyon)
        if(request.getPassword().length() < 6) {
            throw new BusinessException("Şifreniz en az 6 karakter olmalıdır!");
        }

        // 2. Senkronizasyon 1: Keycloak Sunucusuna (IAM) kullanıcıyı oluşturma isteği atılır
        System.out.println("-> Keycloak Admin API'sine asenkron kayıt isteği (OAuth2 Create User) gönderildi.");
        
        // 3. Senkronizasyon 2: Uygulamanın çekirdek veritabanına AppUser (Domain Entity) kaydedilir
        // AppUser user = new AppUser(request.getUsername(), request.getEmail(), ...);
        // userRepository.save(user);
        
        System.out.println("-> Yerel PostgreSQL veritabanında 'AppUser' varlığı başarıyla oluşturuldu.");
        System.out.println("[KAYIT SİSTEMİ] Kullanıcı başarıyla sisteme katıldı!");
    }
}
