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
import tr.com.huseyinaydin.application.ports.ITokenService;

import tr.com.huseyinaydin.infrastructure.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final ITokenService tokenService;

    public SecurityConfig(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/api/**"))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    new AntPathRequestMatcher("/api/v1/auth/**"),
                    new AntPathRequestMatcher("/health"),
                    new AntPathRequestMatcher("/api/Song/**"),
                    new AntPathRequestMatcher("/api/v1/search/**")
                ).permitAll()
            )
            .addFilterBefore(
                new JwtAuthenticationFilter(tokenService),
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
                    new AntPathRequestMatcher("/"),
                    new AntPathRequestMatcher("/index"),
                    new AntPathRequestMatcher("/login"),
                    new AntPathRequestMatcher("/doLogin"),
                    new AntPathRequestMatcher("/register"),
                    new AntPathRequestMatcher("/doRegister"),
                    new AntPathRequestMatcher("/doLogout"),
                    new AntPathRequestMatcher("/recommendations"),
                    new AntPathRequestMatcher("/Song/Search**"),
                    new AntPathRequestMatcher("/search"),
                    new AntPathRequestMatcher("/search.xhtml"),
                    new AntPathRequestMatcher("/likes"),
                    new AntPathRequestMatcher("/likes.xhtml"),
                    new AntPathRequestMatcher("/profile"),
                    new AntPathRequestMatcher("/profile.xhtml"),
                    new AntPathRequestMatcher("/edit-profile"),
                    new AntPathRequestMatcher("/edit-profile.xhtml"),
                    new AntPathRequestMatcher("/artists"),
                    new AntPathRequestMatcher("/artists.xhtml"),
                    new AntPathRequestMatcher("/genres"),
                    new AntPathRequestMatcher("/genres.xhtml"),
                    new AntPathRequestMatcher("/playlists"),
                    new AntPathRequestMatcher("/playlists.xhtml"),
                    new AntPathRequestMatcher("/membership"),
                    new AntPathRequestMatcher("/membership.xhtml"),
                    new AntPathRequestMatcher("/login.xhtml"),
                    new AntPathRequestMatcher("/register.xhtml"),
                    new AntPathRequestMatcher("/error/**"),
                    new AntPathRequestMatcher("/css/**"),
                    new AntPathRequestMatcher("/js/**"),
                    new AntPathRequestMatcher("/images/**"),
                    new AntPathRequestMatcher("/webjars/**"),
                    new AntPathRequestMatcher("/actuator/**"),
                    new AntPathRequestMatcher("/health"),
                    new AntPathRequestMatcher("/swagger-ui/**"),
                    new AntPathRequestMatcher("/swagger-ui.html"),
                    new AntPathRequestMatcher("/v3/api-docs/**"),
                    new AntPathRequestMatcher("/web-api/Like/**")
                ).permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendRedirect(request.getContextPath() + "/login");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect(request.getContextPath() + "/error/403");
                })
            )
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JwtAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
