package tr.com.huseyinaydin.application.song.dtos;

import tr.com.huseyinaydin.domain.song.read.SongReadModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record SongResponse(
    UUID id,
    String title,
    String coverImageUrl,
    String audioUrl,
    long durationSeconds,
    long playCount,
    String tier,
    LocalDateTime releaseDate,
    UUID artistId,
    UUID genreId,
    String artistName,
    String genreName
) implements Serializable {

    /**
     * Backward-compatible 10-parameter constructor for existing callers.
     */
    public SongResponse(UUID id, String title, String coverImageUrl, String audioUrl,
                        long durationSeconds, long playCount, String tier,
                        LocalDateTime releaseDate, UUID artistId, UUID genreId) {
        this(id, title, coverImageUrl, audioUrl, durationSeconds, playCount, tier, releaseDate, artistId, genreId, null, null);
    }

    /**
     * Factory method converting from CQRS read model projection.
     */
    public static SongResponse fromReadModel(SongReadModel readModel) {
        if (readModel == null) {
            return null;
        }
        return new SongResponse(
            readModel.getId(),
            readModel.getTitle(),
            readModel.getCoverImageUrl(),
            readModel.getAudioUrl(),
            readModel.getDurationSeconds(),
            readModel.getPlayCount(),
            readModel.getTier(),
            readModel.getReleaseDate(),
            readModel.getArtistId(),
            readModel.getGenreId(),
            readModel.getArtistName(),
            readModel.getGenreName()
        );
    }
}
