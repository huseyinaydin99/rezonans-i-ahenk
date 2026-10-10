package tr.com.huseyinaydin.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.ITokenService;
import tr.com.huseyinaydin.application.ports.TokenClaims;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

/** HS256 imzalı gerçek JWT üretimi ve doğrulaması. */
@Component
public class JwtTokenProvider implements ITokenService {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtTokenProvider(
            @Value("${music.security.jwt.secret:MusicPlatformSecretKeyReplaceInProductionMusicPlatformSecretKeyReplaceInProduction}") String secret,
            @Value("${music.security.jwt.expiration-seconds:3600}") long expirationSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    @Override
    public String generateAccessToken(String email, String role, String tier) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .claim("tier", tier)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationSeconds * 1000))
                .signWith(key)
                .compact();
    }

    @Override
    public Optional<TokenClaims> parseAccessToken(String token) {
        if (token == null || token.isBlank()) return Optional.empty();
        try {
            Claims c = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            return Optional.of(new TokenClaims(c.getSubject(), c.get("role", String.class), c.get("tier", String.class)));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public long getExpirationSeconds() {
        return expirationSeconds;
    }
}
