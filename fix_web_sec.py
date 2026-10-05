import os

sec = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/SecurityConfig.java'
if os.path.exists(sec):
    with open(sec, 'r') as f:
        content = f.read()
    
    # We will replace the entire file with a combined version
    new_content = """package tr.com.huseyinaydin.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tr.com.huseyinaydin.application.ports.IJwtService;
import tr.com.huseyinaydin.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final IJwtService jwtService;

    public SecurityConfig(IJwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/health").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter((tr.com.huseyinaydin.infrastructure.security.JwtTokenProvider) jwtService),
                UsernamePasswordAuthenticationFilter.class
            );
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login.xhtml", "/register.xhtml", "/error/**", "/resources/**", "/jakarta.faces.resource/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect(request.getContextPath() + "/login.xhtml");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect(request.getContextPath() + "/error/403.xhtml");
                })
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
            
        return http.build();
    }
}
"""
    with open(sec, 'w') as f:
        f.write(new_content)

