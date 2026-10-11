package tr.com.huseyinaydin.infrastructure.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SecureMicroserviceProxy {
    private static final Logger log = LoggerFactory.getLogger(SecureMicroserviceProxy.class);


    private final RestTemplate secureRestTemplate;

    public SecureMicroserviceProxy(@Qualifier("secureRestTemplate") RestTemplate secureRestTemplate) {
        this.secureRestTemplate = secureRestTemplate;
    }

    public String fetchSecureDataFromOtherMicroservice() {
        // Bu metod çağrıldığında, arka planda JwtBearerInterceptor devreye girer
        // ve oturum açmış kullanıcının token'ını otomatik olarak Header'a ekler.
        String targetUrl = "https://internal.musicplatform.com/api/v1/secure-data";
        
        log.info("Uzak mikroservise güvenli (Bearer Token taşıyan) istek atılıyor: " + targetUrl);
        // String response = secureRestTemplate.getForObject(targetUrl, String.class);
        return "Simulated Remote Secure Data";
    }
}
