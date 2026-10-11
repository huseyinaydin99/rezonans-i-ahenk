package tr.com.huseyinaydin.domain.song.read;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CQRS Read Model representing an immutable, denormalized song projection.
 * Optimized specifically for high-throughput, non-locking reads.
 */
public class SongReadModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String title;
    private String coverImageUrl;
    private String audioUrl;
    private long durationSeconds;
    private long playCount;
    private String tier;
    private LocalDateTime releaseDate;
    private UUID artistId;
    private String artistName;
    private UUID genreId;
    private String genreName;
    private LocalDateTime createdDate;

    public SongReadModel() {}

    public SongReadModel(UUID id, String title, String coverImageUrl, String audioUrl,
                         long durationSeconds, long playCount, String tier,
                         LocalDateTime releaseDate, UUID artistId, String artistName,
                         UUID genreId, String genreName, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.audioUrl = audioUrl;
        this.durationSeconds = durationSeconds;
        this.playCount = playCount;
        this.tier = tier;
        this.releaseDate = releaseDate;
        this.artistId = artistId;
        this.artistName = artistName;
        this.genreId = genreId;
        this.genreName = genreName;
        this.createdDate = createdDate;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public void setDurationSeconds(long durationSeconds) {
        this.durationSeconds = durationSeconds;
    }

    public long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(long playCount) {
        this.playCount = playCount;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public LocalDateTime getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDateTime releaseDate) {
        this.releaseDate = releaseDate;
    }

    public UUID getArtistId() {
        return artistId;
    }

    public void setArtistId(UUID artistId) {
        this.artistId = artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public UUID getGenreId() {
        return genreId;
    }

    public void setGenreId(UUID genreId) {
        this.genreId = genreId;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
