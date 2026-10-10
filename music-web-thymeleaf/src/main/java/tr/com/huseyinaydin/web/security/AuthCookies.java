package tr.com.huseyinaydin.web.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.security.JwtAuthenticationFilter;

import java.time.Duration;

/** Access token'ı HttpOnly + SameSite=Lax cookie olarak taşır (JS erişemez, cross-site gönderilmez). */
@Component
public class AuthCookies {

    private final boolean secure;

    public AuthCookies(@Value("${music.security.cookie-secure:false}") boolean secure) {
        this.secure = secure;
    }

    public void write(HttpServletResponse response, String token, long maxAgeSeconds) {
        response.addHeader(HttpHeaders.SET_COOKIE, build(token, Duration.ofSeconds(maxAgeSeconds)).toString());
    }

    public void clear(HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, build("", Duration.ZERO).toString());
    }

    private ResponseCookie build(String value, Duration maxAge) {
        return ResponseCookie.from(JwtAuthenticationFilter.AUTH_COOKIE_NAME, value)
                .httpOnly(true).secure(secure).sameSite("Lax").path("/").maxAge(maxAge).build();
    }
}
