package tr.com.huseyinaydin.domain.song;

import tr.com.huseyinaydin.domain.common.Entity;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Song extends Entity<UUID> {
    private String title;
    private String coverImageUrl;
    private String audioUrl;
    private Duration duration;
    private long playCount;
    private MembershipTier tier;
    private LocalDateTime releaseDate;
    private UUID artistId;
    private UUID genreId;

    protected Song() {
        super();
    }

    public Song(String title, String coverImageUrl, String audioUrl, Duration duration, 
                MembershipTier tier, LocalDateTime releaseDate, UUID artistId, UUID genreId) {
        super();
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

    public void changeTier(MembershipTier newTier) {
        this.tier = newTier;
    }

    public String getTitle() { return title; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getAudioUrl() { return audioUrl; }
    public Duration getDuration() { return duration; }
    public long getPlayCount() { return playCount; }
    public MembershipTier getTier() { return tier; }
    public LocalDateTime getReleaseDate() { return releaseDate; }
    public UUID getArtistId() { return artistId; }
    public UUID getGenreId() { return genreId; }
}
