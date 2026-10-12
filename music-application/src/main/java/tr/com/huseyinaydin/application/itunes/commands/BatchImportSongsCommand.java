package tr.com.huseyinaydin.application.itunes.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.itunes.dtos.BatchImportResultDto;
import tr.com.huseyinaydin.application.itunes.dtos.BatchImportResultDto.ImportedTrackSummary;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesTrackDto;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * iTunes API üzerinden seçili synthwave/retro sanatçılar için toplu parça, albüm kapağı
 * ve ses önizlemelerini mükerrer kontrolüyle kataloğa ve veritabanına aktaran CQRS komutu.
 */
public record BatchImportSongsCommand(
        List<String> artists,
        Integer limitPerArtist
) implements ICommand<BatchImportResultDto> {

    public static final List<String> DEFAULT_ARTISTS = List.of(
            "The Midnight",
            "Kavinsky",
            "FM-84",
            "Gunship",
            "Lazerhawk",
            "Timecop1983",
            "Carpenter Brut"
    );

    @Component
    public static class Handler implements ICommandHandler<BatchImportSongsCommand, BatchImportResultDto> {
        private static final Logger log = LoggerFactory.getLogger(Handler.class);

        private final IItunesService itunesService;
        private final ISongRepository songRepository;
        private final ISongSearchService songSearchService;

        public Handler(
                IItunesService itunesService,
                @Autowired(required = false) ISongRepository songRepository,
                @Autowired(required = false) ISongSearchService songSearchService) {
            this.itunesService = itunesService;
            this.songRepository = songRepository;
            this.songSearchService = songSearchService;
        }

        @Override
        public BatchImportResultDto handle(BatchImportSongsCommand command) {
            List<String> targetArtists = (command != null && command.artists() != null && !command.artists().isEmpty())
                    ? command.artists()
                    : DEFAULT_ARTISTS;
            int limit = (command != null && command.limitPerArtist() != null && command.limitPerArtist() > 0)
                    ? command.limitPerArtist()
                    : 5;

            log.info("🚀 [TOPLU İÇE AKTARMA] Başlatılıyor. Hedef Sanatçı Sayısı: {}, Sanatçı Başı Limit: {}",
                    targetArtists.size(), limit);

            Set<String> existingSongKeys = new HashSet<>();
            // Mevcut şarkıları alarak mükerrerliği önle
            if (songSearchService != null) {
                try {
                    List<Map<String, Object>> existing = songSearchService.searchSongs("");
                    if (existing != null) {
                        for (Map<String, Object> s : existing) {
                            String t = (String) s.get("title");
                            String a = (String) s.get("artist");
                            if (t != null) {
                                existingSongKeys.add(createKey(t, a));
                            }
                        }
                    }
                } catch (Exception ex) {
                    log.warn("Mevcut şarkı listesi alınırken uyarı: {}", ex.getMessage());
                }
            }

            int totalFound = 0;
            int totalImported = 0;
            int totalSkipped = 0;
            List<ImportedTrackSummary> importedList = new ArrayList<>();
            List<String> processLogs = new ArrayList<>();

            long nextIdSeed = 1000L + (System.currentTimeMillis() % 100000L);

            MembershipTier[] tiers = new MembershipTier[] {
                    MembershipTier.BASIC,
                    MembershipTier.GOLD,
                    MembershipTier.PREMIUM,
                    MembershipTier.ELIT
            };

            for (String artistName : targetArtists) {
                if (artistName == null || artistName.trim().isEmpty()) continue;
                String trimmedArtist = artistName.trim();
                log.info("🔎 iTunes sorgulanıyor: '{}' (Limit: {})", trimmedArtist, limit);
                processLogs.add("Sanatçı taranıyor: " + trimmedArtist);

                try {
                    ItunesResponseDto response = itunesService.searchMusic(trimmedArtist, limit);
                    if (response == null || response.results() == null || response.results().isEmpty()) {
                        processLogs.add("⚠️ '" + trimmedArtist + "' için parça bulunamadı.");
                        continue;
                    }

                    int artistFound = response.results().size();
                    totalFound += artistFound;

                    for (ItunesTrackDto track : response.results()) {
                        if (track == null || track.trackName() == null || track.trackName().trim().isEmpty()) {
                            continue;
                        }

                        String effectiveArtist = (track.artistName() != null && !track.artistName().trim().isEmpty())
                                ? track.artistName().trim()
                                : trimmedArtist;
                        String key = createKey(track.trackName(), effectiveArtist);

                        if (existingSongKeys.contains(key)) {
                            totalSkipped++;
                            processLogs.add("⏭️ Mükerrer kayıt atlandı: " + effectiveArtist + " - " + track.trackName());
                            continue;
                        }

                        // Mükerrer setine kaydet
                        existingSongKeys.add(key);
                        totalImported++;

                        // 1. Tür eşleme
                        String genreName = resolveGenre(track.primaryGenreName(), effectiveArtist);

                        // 2. Paket seviyesi dağılımı (Döngüsel adil dağıtım)
                        MembershipTier assignedTier = tiers[(totalImported - 1) % tiers.length];

                        // 3. Görsel yükseltme (100x100 -> 600x600)
                        String artwork = (track.artworkUrl100() != null)
                                ? track.artworkUrl100().replace("100x100", "600x600")
                                : "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600";

                        // 4. Süre formatlama
                        long millis = track.trackTimeMillis() > 0 ? track.trackTimeMillis() : 210000L;
                        Duration duration = Duration.ofMillis(millis);
                        long mins = duration.toMinutes();
                        long secs = duration.toSeconds() % 60;
                        String durationStr = String.format("%02d:%02d", mins, secs);

                        // 5. Veritabanı Domain Entity kaydı
                        if (songRepository != null) {
                            try {
                                Song songEntity = Song.builder()
                                        .title(track.trackName().trim())
                                        .coverImageUrl(artwork)
                                        .audioUrl(track.previewUrl())
                                        .duration(duration)
                                        .tier(assignedTier)
                                        .releaseDate(LocalDateTime.now())
                                        .build();
                                songRepository.save(songEntity);
                            } catch (Exception repoEx) {
                                log.warn("Veritabanı varlık kaydı atlandı: {}", repoEx.getMessage());
                            }
                        }

                        // 6. Dinamik arama ve çalma kataloğuna anlık eşzamanlama
                        Long generatedId = nextIdSeed + totalImported;
                        if (songSearchService != null) {
                            songSearchService.registerSong(
                                    generatedId,
                                    track.trackName().trim(),
                                    effectiveArtist,
                                    genreName,
                                    assignedTier.name(),
                                    artwork,
                                    durationStr
                            );
                        }

                        ImportedTrackSummary summary = new ImportedTrackSummary(
                                track.trackName().trim(),
                                effectiveArtist,
                                genreName,
                                assignedTier.name(),
                                durationStr,
                                artwork,
                                track.previewUrl()
                        );
                        importedList.add(summary);

                        processLogs.add("✅ Eklendi: " + effectiveArtist + " - " + track.trackName().trim()
                                + " [" + genreName + " | " + assignedTier.name() + "]");
                    }
                } catch (Exception e) {
                    log.error("Toplu içe aktarma hatası ({}): {}", trimmedArtist, e.getMessage());
                    processLogs.add("❌ Hata (" + trimmedArtist + "): " + e.getMessage());
                }
            }

            log.info("🏁 [TOPLU İÇE AKTARMA TAMAMLANDI] İşlenen Sanatçı: {}, Bulunan: {}, Eklenen: {}, Atlanan: {}",
                    targetArtists.size(), totalFound, totalImported, totalSkipped);

            return new BatchImportResultDto(
                    targetArtists.size(),
                    totalFound,
                    totalImported,
                    totalSkipped,
                    importedList,
                    processLogs
            );
        }

        private String createKey(String title, String artist) {
            String t = title != null ? title.trim().toLowerCase(Locale.ROOT) : "";
            String a = artist != null ? artist.trim().toLowerCase(Locale.ROOT) : "";
            return t + "|" + a;
        }

        private String resolveGenre(String primaryGenre, String artist) {
            String combined = ((primaryGenre != null ? primaryGenre : "") + " " + (artist != null ? artist : "")).toLowerCase(Locale.ROOT);
            if (combined.contains("synth") || combined.contains("retro") || combined.contains("midnight")
                    || combined.contains("kavinsky") || combined.contains("gunship") || combined.contains("lazerhawk")
                    || combined.contains("timecop") || combined.contains("carpenter") || combined.contains("fm-84") || combined.contains("fm84")) {
                return "Synthwave";
            } else if (combined.contains("rock") || combined.contains("metal")) {
                return "Rock";
            } else if (combined.contains("jazz") || combined.contains("caz")) {
                return "Jazz";
            } else if (combined.contains("pop")) {
                return "Pop";
            } else if (combined.contains("electronic") || combined.contains("dance") || combined.contains("house")) {
                return "Elektronik";
            }
            return (primaryGenre != null && !primaryGenre.trim().isEmpty()) ? primaryGenre.trim() : "Synthwave";
        }
    }
}
