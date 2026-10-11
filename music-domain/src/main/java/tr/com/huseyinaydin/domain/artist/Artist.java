package tr.com.huseyinaydin.domain.artist;

import java.util.UUID;

public class Artist extends tr.com.huseyinaydin.domain.common.BaseEntity<java.util.UUID> {
    private UUID id;
    private String name;
    private String bio;
    private String profileImageUrl;
    private long followerCount;

    private String imageUrl;
    private long monthlyListeners;
    private boolean isVerified;

    protected Artist() {}

    private Artist(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.bio = builder.bio;
        this.profileImageUrl = builder.profileImageUrl;
        this.imageUrl = builder.profileImageUrl;
        this.followerCount = 0;
        this.monthlyListeners = 0;
        this.isVerified = false;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String bio;
        private String profileImageUrl;
        private long monthlyListeners;
        private boolean isVerified;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder profileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; return this; }
        public Builder monthlyListeners(long monthlyListeners) { this.monthlyListeners = monthlyListeners; return this; }
        public Builder isVerified(boolean isVerified) { this.isVerified = isVerified; return this; }

        public Artist build() {
            if (name == null) throw new IllegalStateException("Sanatçı adı zorunludur!");
            Artist artist = new Artist(this);
            artist.monthlyListeners = this.monthlyListeners;
            artist.isVerified = this.isVerified;
            return artist;
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getImageUrl() { return imageUrl != null ? imageUrl : profileImageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; this.profileImageUrl = imageUrl; }

    public String getProfileImageUrl() { return getImageUrl(); }
    public void setProfileImageUrl(String profileImageUrl) { setImageUrl(profileImageUrl); }

    public long getMonthlyListeners() { return monthlyListeners; }
    public void setMonthlyListeners(long monthlyListeners) { this.monthlyListeners = monthlyListeners; }

    public boolean isVerified() { return isVerified; }
    public boolean getIsVerified() { return isVerified; }
    public void setVerified(boolean verified) { this.isVerified = verified; }
    public void setIsVerified(boolean isVerified) { this.isVerified = isVerified; }

    public long getFollowerCount() { return followerCount; }
    public void setFollowerCount(long followerCount) { this.followerCount = followerCount; }
}