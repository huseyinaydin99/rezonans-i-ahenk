package tr.com.huseyinaydin.application.ports;

import java.util.Optional;

/** Stateless kimlik doğrulama için erişim token'ı üretimi ve doğrulaması (Port). */
public interface ITokenService {
    String generateAccessToken(String email, String role, String tier);

    /** Token geçerliyse (imza + süre) içindeki claim'leri döner, aksi halde boş döner. */
    Optional<TokenClaims> parseAccessToken(String token);

    long getExpirationSeconds();
}
