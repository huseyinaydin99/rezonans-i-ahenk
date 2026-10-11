package tr.com.huseyinaydin.infrastructure.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IPasswordHashService;
import tr.com.huseyinaydin.application.ports.PasswordHash;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

@Service
public class BcryptPasswordHashService implements IPasswordHashService {

    private final PasswordEncoder passwordEncoder;

    public BcryptPasswordHashService() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Work factor 12
    }

    @Override
    public PasswordHash createHash(String plainPassword) {
        String encoded = passwordEncoder.encode(plainPassword);
        // BCrypt formatı: $2a$12$[22 karakterlik Salt][31 karakterlik Hash] (Toplam 60 karakter)
        // Domain modelinde ve veri şemasında tuzun (Salt) boş kalmaması için 22 karakterlik tuzu ayıklıyoruz.
        byte[] salt;
        if (encoded != null && encoded.length() >= 29) {
            salt = encoded.substring(7, 29).getBytes(StandardCharsets.UTF_8);
        } else {
            salt = new byte[16];
            new SecureRandom().nextBytes(salt);
        }
        return new PasswordHash(encoded.getBytes(StandardCharsets.UTF_8), salt);
    }

    @Override
    public boolean verifyHash(String plainPassword, PasswordHash passwordHash) {
        if (passwordHash == null || passwordHash.hash() == null || plainPassword == null) {
            return false;
        }
        String encoded = new String(passwordHash.hash(), StandardCharsets.UTF_8);
        return passwordEncoder.matches(plainPassword, encoded);
    }
}
