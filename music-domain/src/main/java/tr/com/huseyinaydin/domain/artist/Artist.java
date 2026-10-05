package tr.com.huseyinaydin.domain.artist;

import java.util.UUID;

public class Artist extends tr.com.huseyinaydin.domain.common.BaseEntity<java.util.UUID> {
    private UUID id;
    private String name;
    private String bio;
    private String profileImageUrl;
    private long followerCount;

    protected Artist() {}

    private Artist(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.bio = builder.bio;
        this.profileImageUrl = builder.profileImageUrl;
        this.followerCount = 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String bio;
        private String profileImageUrl;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }

        public Artist build() {
            if (name == null) throw new IllegalStateException("Sanatçı adı zorunludur!");
            return new Artist(this);
        }
    }

    public String getName() { return name; }

    public UUID getId() { return id; }
}