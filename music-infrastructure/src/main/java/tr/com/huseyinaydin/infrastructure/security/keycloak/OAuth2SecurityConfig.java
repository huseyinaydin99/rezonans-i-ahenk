package tr.com.huseyinaydin.infrastructure.security.keycloak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class OAuth2SecurityConfig {
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8180/realms/music-realm/protocol/openid-connect/certs").build();
    }


    private final KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter;

    public OAuth2SecurityConfig(KeycloakJwtAuthenticationConverter keycloakJwtAuthenticationConverter) {
        this.keycloakJwtAuthenticationConverter = keycloakJwtAuthenticationConverter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("[GÜVENLİK] OAuth2 Resource Server ve Keycloak entegrasyonu devrede!");
        
        http
            .csrf(AbstractHttpConfigurer::disable) // Token tabanlı olduğu için CSRF kapalı
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)
                )
            );
            
        return http.build();
    }
}
