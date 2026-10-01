package tr.com.huseyinaydin.domain.user;

import tr.com.huseyinaydin.domain.enums.MembershipTier;
import java.util.UUID;
import java.time.LocalDateTime;

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

    private AppUser(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.username = builder.username;
        this.email = builder.email;
        this.passwordHash = builder.passwordHash;
        this.bio = builder.bio;
        this.profileImageUrl = builder.profileImageUrl;
        this.tier = builder.tier != null ? builder.tier : MembershipTier.BASIC;
        this.registeredAt = builder.registeredAt != null ? builder.registeredAt : LocalDateTime.now();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String username;
        private String email;
        private String passwordHash;
        private String bio;
        private String profileImageUrl;
        private MembershipTier tier;
        private LocalDateTime registeredAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }
        public Builder tier(MembershipTier tier) { this.tier = tier; return this; }

        public AppUser build() {
            if (username == null || email == null) {
                throw new IllegalStateException("Zorunlu alanlar (username, email) eksik!");
            }
            return new AppUser(this);
        }
    }
}
