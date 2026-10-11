package tr.com.huseyinaydin.domain.token;





import java.time.LocalDateTime;
import java.util.UUID;



public class RefreshToken {

    private UUID id;

    private UUID userId;

    private String token;

    private LocalDateTime expiration;

    private boolean isRevoked;

    protected RefreshToken() {}

    public RefreshToken(UUID userId, String token, LocalDateTime expiration) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.token = token;
        this.expiration = expiration;
        this.isRevoked = false;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getExpiration() { return expiration; }
    public void setExpiration(LocalDateTime expiration) { this.expiration = expiration; }

    public boolean isRevoked() { return isRevoked; }
    public boolean getIsRevoked() { return isRevoked; }
    public void setIsRevoked(boolean isRevoked) { this.isRevoked = isRevoked; }
    public void setRevoked(boolean revoked) { this.isRevoked = revoked; }

    public void revoke() { this.isRevoked = true; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiration);
    }
}
