package tr.com.huseyinaydin.domain.artist;

import tr.com.huseyinaydin.domain.common.Entity;
import java.util.UUID;

public class Artist extends Entity<UUID> {
    private String name;
    private String imageUrl;
    private String bio;
    private long monthlyListeners;
    private boolean isVerified;

    protected Artist() {
        super();
    }

    public Artist(String name, String imageUrl, String bio) {
        super();
        this.id = UUID.randomUUID();
        this.name = name;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.monthlyListeners = 0;
        this.isVerified = false;
    }

    public void verify() {
        this.isVerified = true;
    }

    public void updateProfile(String name, String imageUrl, String bio) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.bio = bio;
    }

    public void updateMonthlyListeners(long count) {
        if (count < 0) throw new IllegalArgumentException("Listeners count cannot be negative");
        this.monthlyListeners = count;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getBio() { return bio; }
    public long getMonthlyListeners() { return monthlyListeners; }
    public boolean isVerified() { return isVerified; }
}
