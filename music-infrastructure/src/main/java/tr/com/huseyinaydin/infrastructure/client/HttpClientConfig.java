package tr.com.huseyinaydin.infrastructure.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class HttpClientConfig {

    private final JwtBearerInterceptor jwtBearerInterceptor;

    public HttpClientConfig(JwtBearerInterceptor jwtBearerInterceptor) {
        this.jwtBearerInterceptor = jwtBearerInterceptor;
    }

    @Bean(name = "secureRestTemplate")
    public RestTemplate secureRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        
        // Interceptor'ları alıp JWT enjekte edici sınıfımızı ekliyoruz
        List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        if (interceptors == null) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(jwtBearerInterceptor);
        restTemplate.setInterceptors(interceptors);
        
        return restTemplate;
    }
}
