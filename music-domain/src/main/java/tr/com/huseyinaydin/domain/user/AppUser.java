package tr.com.huseyinaydin.domain.user;

import tr.com.huseyinaydin.domain.enums.MembershipTier;
import java.util.UUID;
import java.time.LocalDateTime;

// Saf (Pure) Domain Entity
public class AppUser {
    private UUID id;
    private String username;
    private String email;
    private String passwordHash;
    private String bio;
    private String profileImageUrl;
    private MembershipTier tier;
    private LocalDateTime registeredAt;

    protected AppUser() {}

    public AppUser(String username, String email, String passwordHash) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.tier = MembershipTier.BASIC;
        this.registeredAt = LocalDateTime.now();
    }

    // Getters and Business Methods omitted for brevity
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public MembershipTier getTier() { return tier; }
    public void setTier(MembershipTier tier) { this.tier = tier; }
}
