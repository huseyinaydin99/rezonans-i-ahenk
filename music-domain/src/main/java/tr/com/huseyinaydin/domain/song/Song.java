package tr.com.huseyinaydin.domain.song;

import tr.com.huseyinaydin.domain.enums.MembershipTier;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class Song extends tr.com.huseyinaydin.domain.common.BaseEntity<java.util.UUID> {
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

    protected Song() {}

    private Song(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.title = builder.title;
        this.coverImageUrl = builder.coverImageUrl;
        this.audioUrl = builder.audioUrl;
        this.duration = builder.duration;
        this.tier = builder.tier != null ? builder.tier : MembershipTier.BASIC;
        this.releaseDate = builder.releaseDate != null ? builder.releaseDate : LocalDateTime.now();
        this.artistId = builder.artistId;
        this.genreId = builder.genreId;
        this.playCount = 0;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String title;
        private String coverImageUrl;
        private String audioUrl;
        private Duration duration;
        private MembershipTier tier;
        private LocalDateTime releaseDate;
        private UUID artistId;
        private UUID genreId;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder coverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; return this; }
        public Builder audioUrl(String audioUrl) { this.audioUrl = audioUrl; return this; }
        public Builder duration(Duration duration) { this.duration = duration; return this; }
        public Builder tier(MembershipTier tier) { this.tier = tier; return this; }
        public Builder releaseDate(LocalDateTime releaseDate) { this.releaseDate = releaseDate; return this; }
        public Builder artistId(UUID artistId) { this.artistId = artistId; return this; }
        public Builder genreId(UUID genreId) { this.genreId = genreId; return this; }

        public Song build() {
            if (title == null || audioUrl == null || artistId == null) {
                throw new IllegalStateException("Zorunlu alanlar (title, audioUrl, artistId) eksik!");
            }
            return new Song(this);
        }
    }

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public String getAudioUrl() { return audioUrl; }
    public Duration getDuration() { return duration; }
    public MembershipTier getTier() { return tier; }
    public LocalDateTime getReleaseDate() { return releaseDate; }
    public UUID getArtistId() { return artistId; }
    public UUID getGenreId() { return genreId; }
    public long getPlayCount() { return playCount; }
    public void incrementPlayCount() { this.playCount++; }
}
