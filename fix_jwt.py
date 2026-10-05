with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/keycloak/OAuth2SecurityConfig.java', 'r') as f:
    content = f.read()

content = content.replace('import org.springframework.security.web.SecurityFilterChain;', '''import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
''')

content = content.replace('public class OAuth2SecurityConfig {', '''public class OAuth2SecurityConfig {
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:8180/realms/music-realm/protocol/openid-connect/certs").build();
    }
''')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/keycloak/OAuth2SecurityConfig.java', 'w') as f:
    f.write(content)
