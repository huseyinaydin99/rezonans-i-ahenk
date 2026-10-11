package tr.com.huseyinaydin.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tr.com.huseyinaydin.infrastructure.security.JwtTokenProvider;

import tr.com.huseyinaydin.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;


    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {

        this.jwtTokenProvider = jwtTokenProvider;

    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/api/**"))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/api/v1/auth/**"), new AntPathRequestMatcher("/health"), new AntPathRequestMatcher("/api/Song/Play/**"), new AntPathRequestMatcher("/api/Song/Search**")).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
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
                .requestMatchers(
                    new AntPathRequestMatcher("/login.xhtml"),
                    new AntPathRequestMatcher("/register.xhtml"),
                    new AntPathRequestMatcher("/actuator/**"),
                    new AntPathRequestMatcher("/health"),
                    new AntPathRequestMatcher("/swagger-ui/**"),
                    new AntPathRequestMatcher("/swagger-ui.html"),
                    new AntPathRequestMatcher("/v3/api-docs/**"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/error/**"),
                    new AntPathRequestMatcher("/resources/**"),
                    new AntPathRequestMatcher("/jakarta.faces.resource/**")
                ).permitAll()
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
