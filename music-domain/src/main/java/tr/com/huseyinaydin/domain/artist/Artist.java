package tr.com.huseyinaydin.domain.artist;

import java.util.UUID;

// Saf (Pure) Domain Entity
public class Artist {
    private UUID id;
    private String name;
    private String bio;
    private String profileImageUrl;
    private long followerCount;

    protected Artist() {}

    public Artist(String name, String bio, String profileImageUrl) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.bio = bio;
        this.profileImageUrl = profileImageUrl;
        this.followerCount = 0;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public long getFollowerCount() { return followerCount; }
}
