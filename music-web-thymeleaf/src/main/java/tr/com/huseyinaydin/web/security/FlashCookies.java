package tr.com.huseyinaydin.web.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/** Redirect sonrası tek seferlik mesajlar için kısa ömürlü cookie (HttpSession gerektirmez). */
public final class FlashCookies {
    private static final String PREFIX = "FLASH_";

    private FlashCookies() {}

    public static void set(HttpServletResponse response, String key, String message) {
        String value = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(message.getBytes(StandardCharsets.UTF_8));
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(PREFIX + key, value)
                .httpOnly(true).sameSite("Lax").path("/").maxAge(Duration.ofSeconds(30)).build().toString());
    }

    /** Mesajı okur ve cookie'yi siler; yoksa null döner. */
    public static String consume(HttpServletRequest request, HttpServletResponse response, String key) {
        if (request.getCookies() == null) return null;
        for (Cookie c : request.getCookies()) {
            if ((PREFIX + key).equals(c.getName())) {
                response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(PREFIX + key, "")
                        .httpOnly(true).sameSite("Lax").path("/").maxAge(Duration.ZERO).build().toString());
                try {
                    return new String(Base64.getUrlDecoder().decode(c.getValue()), StandardCharsets.UTF_8);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
        return null;
    }
}
