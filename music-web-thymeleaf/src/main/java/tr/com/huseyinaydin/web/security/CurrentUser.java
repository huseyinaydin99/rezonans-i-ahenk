package tr.com.huseyinaydin.web.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tr.com.huseyinaydin.application.ports.TokenClaims;

import java.util.Optional;

/** Geçerli isteğin kimliğini (token claim'leri) döner; oturum kullanılmaz. */
public final class CurrentUser {
    private CurrentUser() {}

    public static Optional<TokenClaims> get() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            if (auth.getPrincipal() instanceof TokenClaims claims) {
                return Optional.of(claims);
            }
            if (auth.getName() != null && !auth.getName().isBlank() && !"anonymousUser".equalsIgnoreCase(auth.getName())) {
                return Optional.of(new TokenClaims(auth.getName(), "CUSTOMER", "Standard"));
            }
        }
        return Optional.empty();
    }
}
