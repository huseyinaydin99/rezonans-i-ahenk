package tr.com.huseyinaydin.application.song.dtos;

import java.util.UUID;
import java.time.LocalDateTime;

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
    UUID genreId
) {}
