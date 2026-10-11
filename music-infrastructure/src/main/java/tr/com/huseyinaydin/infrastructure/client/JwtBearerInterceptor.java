package tr.com.huseyinaydin.infrastructure.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtBearerInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger log = LoggerFactory.getLogger(JwtBearerInterceptor.class);


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        // Eğer mevcut kullanıcının bir JWT yetkisi (Credentials) varsa, dış API isteklerine Bearer olarak ekle
        if (authentication != null && authentication.getCredentials() instanceof String jwtToken) {
            log.info("Giden uzak (Remote) HTTP isteğine JWT Bearer Token ekleniyor...");
            request.getHeaders().add(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken);
        }
        
        return execution.execute(request, body);
    }
}
