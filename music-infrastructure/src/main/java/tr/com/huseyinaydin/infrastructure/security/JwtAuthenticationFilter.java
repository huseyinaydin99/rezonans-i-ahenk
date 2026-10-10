package tr.com.huseyinaydin.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import tr.com.huseyinaydin.application.ports.ITokenService;

import java.io.IOException;
import java.util.Collections;

/**
 * Her istekte token'ı doğrular (Authorization: Bearer ... veya HttpOnly cookie).
 * Oturum (HttpSession) kullanmaz; kimlik her istekte token'dan yeniden kurulur.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTH_COOKIE_NAME = "MUSIC_AUTH";

    private final ITokenService tokenService;

    public JwtAuthenticationFilter(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null) {
            tokenService.parseAccessToken(token).ifPresent(claims -> {
                var authorities = claims.role() == null
                        ? Collections.<SimpleGrantedAuthority>emptyList()
                        : Collections.singletonList(new SimpleGrantedAuthority(claims.role()));
                var authentication = new UsernamePasswordAuthenticationToken(claims, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (AUTH_COOKIE_NAME.equals(c.getName())) return c.getValue();
            }
        }
        return null;
    }
}
