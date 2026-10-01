package tr.com.huseyinaydin.domain.song;

import tr.com.huseyinaydin.domain.enums.MembershipTier;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

// Saf (Pure) Domain Entity - Hiçbir Framework/JPA bağımlılığı yok!
public class Song {
    private UUID id;
    private String title;
    private String coverImageUrl;
    private String audioUrl;
    private Duration duration;
    private MembershipTier tier;
    private LocalDateTime releaseDate;
    private UUID artistId;
    private UUID genreId;
    private long playCount;

    // Hibernate proxy için boş yapıcı metod (default constructor)
    protected Song() {}

    public Song(String title, String coverImageUrl, String audioUrl, Duration duration, 
                MembershipTier tier, LocalDateTime releaseDate, UUID artistId, UUID genreId) {
        this.id = UUID.randomUUID();
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.audioUrl = audioUrl;
        this.duration = duration;
        this.tier = tier;
        this.releaseDate = releaseDate;
        this.artistId = artistId;
        this.genreId = genreId;
        this.playCount = 0;
    }

    public void incrementPlayCount() {
        this.playCount++;
    }

    // Getters and Setters omitted for brevity...
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getAudioUrl() { return audioUrl; }
    public Duration getDuration() { return duration; }
    public MembershipTier getTier() { return tier; }
    public long getPlayCount() { return playCount; }
}
