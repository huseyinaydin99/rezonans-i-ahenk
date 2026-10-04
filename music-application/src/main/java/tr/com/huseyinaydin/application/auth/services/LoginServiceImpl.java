package tr.com.huseyinaydin.application.auth.services;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.ILoginService;
import tr.com.huseyinaydin.application.auth.dtos.LoginRequestDto;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import java.util.UUID;

@Service
public class LoginServiceImpl implements ILoginService {

    @Override
    public ResponseTokenDto authenticate(LoginRequestDto request) {
        System.out.println("[GİRİŞ SİSTEMİ] '" + request.getEmail() + "' için Keycloak (IAM) sunucusuna yetki doğrulama isteği atılıyor...");
        
        // Simülasyon: Hatalı giriş kontrolü
        if (request.getPassword() == null || request.getPassword().length() < 3) {
            System.err.println("[GİRİŞ SİSTEMİ] Hatalı şifre denemesi!");
            throw new BusinessException("E-posta veya şifre hatalı!");
        }

        System.out.println("-> Keycloak doğrulandı. JWT Access Token ve Refresh Token üretiliyor.");
        
        // Gerçekte Keycloak'tan dönen JSON parse edilir
        String mockJwtToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9." + UUID.randomUUID().toString() + ".signature_mock";
        String mockRefreshToken = UUID.randomUUID().toString();
        
        System.out.println("[GİRİŞ SİSTEMİ] Kullanıcı başarıyla sisteme bağlandı. Oturum (Session) başlatılıyor.");
        return new ResponseTokenDto(mockJwtToken, mockRefreshToken, 3600, "ROLE_USER");
    }
}
