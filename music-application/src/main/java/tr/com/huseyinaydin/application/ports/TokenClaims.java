package tr.com.huseyinaydin.application.ports;

/** Doğrulanmış bir erişim token'ının taşıdığı kimlik bilgileri. */
public record TokenClaims(String email, String role, String tier) {}
