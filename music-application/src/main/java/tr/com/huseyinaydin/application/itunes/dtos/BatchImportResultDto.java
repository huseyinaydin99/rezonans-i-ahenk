package tr.com.huseyinaydin.application.itunes.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * iTunes toplu içe aktarma işleminin özet sonuç ve rapor DTO'su.
 */
public record BatchImportResultDto(
    int totalArtistsProcessed,
    int totalFound,
    int totalImported,
    int totalSkipped,
    List<ImportedTrackSummary> importedTracks,
    List<String> logs
) implements Serializable {

    public record ImportedTrackSummary(
        String title,
        String artist,
        String genre,
        String tier,
        String duration,
        String coverUrl,
        String previewUrl
    ) implements Serializable {}
}
