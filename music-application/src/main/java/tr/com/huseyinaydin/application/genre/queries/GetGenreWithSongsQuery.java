package tr.com.huseyinaydin.application.genre.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.genre.dtos.GenreSongDto;
import tr.com.huseyinaydin.application.genre.dtos.GenreWithSongsDto;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.domain.genre.Genre;
import tr.com.huseyinaydin.domain.repositories.IGenreRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.*;

/**
 * Belirtilen türe (Genre) ait ayrıntılı meta bilgileri ve o türe ait tüm parçaları getiren CQRS sorgusu.
 */
public record GetGenreWithSongsQuery(String genreId) implements IQuery<GenreWithSongsDto>, ICacheableRequest {
    private static final Logger log = LoggerFactory.getLogger(GetGenreWithSongsQuery.class);

    @Override
    public String getCacheKey() {
        return "genre:with-songs:" + (genreId != null ? genreId.trim().toLowerCase(Locale.ROOT) : "default");
    }

    @Override
    public String getCacheName() {
        return "genresCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 30;
    }

    @Component
    public static class Handler implements IQueryHandler<GetGenreWithSongsQuery, GenreWithSongsDto> {
        private final ISongSearchService songSearchService;
        private final IGenreRepository genreRepository;

        public Handler(@Autowired(required = false) ISongSearchService songSearchService,
                       @Autowired(required = false) IGenreRepository genreRepository) {
            this.songSearchService = songSearchService;
            this.genreRepository = genreRepository;
        }

        @Override
        public GenreWithSongsDto handle(GetGenreWithSongsQuery query) {
            String rawId = query.genreId() != null ? query.genreId().trim() : "";
            log.info("Executing GetGenreWithSongsQuery for genreId: '{}'", rawId);

            if (rawId.isEmpty()) {
                return null;
            }

            // 1. Veritabanında UUID bazlı arama (Entity mevcutsa)
            String dbGenreName = null;
            if (genreRepository != null) {
                try {
                    UUID uuid = UUID.fromString(rawId);
                    Optional<Genre> genreOpt = genreRepository.findById(uuid);
                    if (genreOpt.isPresent()) {
                        dbGenreName = genreOpt.get().getName();
                    }
                } catch (IllegalArgumentException ignored) {
                    // UUID formatında değil
                }
            }

            // 2. Tür meta-bilgi çözümlemesi
            GenreMetadata meta = resolveMetadata(rawId, dbGenreName);

            // 3. Şarkıların katalogdan ve arama servisinden çekilmesi
            List<GenreSongDto> songList = new ArrayList<>();
            if (songSearchService != null) {
                List<Map<String, Object>> searchResults = songSearchService.searchSongs(meta.name);
                for (Map<String, Object> map : searchResults) {
                    String songGenre = (String) map.get("genre");
                    if (songGenre != null && songGenre.equalsIgnoreCase(meta.name)) {
                        Long songId = 0L;
                        Object idObj = map.get("id");
                        if (idObj instanceof Number num) {
                            songId = num.longValue();
                        } else if (idObj != null) {
                            try {
                                songId = Long.parseLong(idObj.toString());
                            } catch (NumberFormatException ignored) {}
                        }

                        String title = (String) map.getOrDefault("title", "Bilinmeyen Şarkı");
                        String artist = (String) map.getOrDefault("artist", "Bilinmeyen Sanatçı");
                        String cover = (String) map.getOrDefault("cover", "/images/default-cover.svg");
                        String duration = (String) map.getOrDefault("duration", "3:30");
                        String tier = (String) map.getOrDefault("tier", "Basic");
                        int tierLevel = toTierLevel(tier);
                        long playCount = 750000L + (songId * 65000L);

                        songList.add(new GenreSongDto(
                            songId,
                            title,
                            artist,
                            cover,
                            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
                            duration,
                            playCount,
                            tier,
                            tierLevel,
                            meta.name
                        ));
                    }
                }
            }

            return new GenreWithSongsDto(
                meta.id,
                meta.name,
                meta.tag,
                meta.description,
                meta.themeClass,
                meta.accentColor,
                songList.size(),
                songList
            );
        }

        private GenreMetadata resolveMetadata(String input, String dbName) {
            String key = (dbName != null ? dbName : input).toLowerCase(Locale.ROOT).trim();

            if (key.equals("pop") || key.equals("1")) {
                return new GenreMetadata("pop", "Pop", "#POP",
                    "Radyo dalgalarını titreten, akılda kalıcı melodiler ve dans pistlerini ısıtan modern ritimler.",
                    "gn-card--pop", "#ec4899");
            }
            if (key.equals("rock") || key.equals("2")) {
                return new GenreMetadata("rock", "Rock", "#ROCK",
                    "Distorsiyonlu gitarlar, güçlü davul vuruşları ve zamana meydan okuyan efsanevi rock klasikleri.",
                    "gn-card--rock", "#00e3fd");
            }
            if (key.equals("jazz") || key.equals("3")) {
                return new GenreMetadata("jazz", "Jazz", "#JAZZ",
                    "Geceye sızan sıcak saksafon ezgileri, doğaçlama piyano akorları ve sofistike caz tınıları.",
                    "gn-card--jazz", "#6366f1");
            }
            if (key.equals("elektronik") || key.equals("4") || key.contains("electro")) {
                return new GenreMetadata("elektronik", "Elektronik", "#ELEKTRONIK",
                    "Derin bas hatları, hipnotik arpejler ve kulüpleri sallayan fütüristik synthesizer kompozisyonları.",
                    "gn-card--elektronik", "#facc15");
            }
            if (key.equals("synthwave") || key.equals("5") || key.contains("synth")) {
                return new GenreMetadata("synthwave", "Synthwave", "#SYNTHWAVE",
                    "Neon ışıklarının ve 80'ler analog synthesizer frekanslarının gelecekle buluştuğu retro-fütüristik ses evreni.",
                    "gn-card--synthwave", "#ff2a85");
            }

            // Genel Fallback
            String formattedName = capitalize(input.replace("-", " "));
            return new GenreMetadata(input.toLowerCase(Locale.ROOT), formattedName, "#" + formattedName.toUpperCase(Locale.ROOT),
                "Frekans ızgarasında yankılanan özel müzik boyutu ve melodik koleksiyon.",
                "gn-card--synthwave", "#ff479c");
        }

        private int toTierLevel(String tier) {
            if (tier == null) return 1;
            return switch (tier.toUpperCase(Locale.ROOT)) {
                case "GOLD" -> 2;
                case "PREMIUM" -> 3;
                case "ELIT", "ELITE" -> 4;
                default -> 1;
            };
        }

        private String capitalize(String text) {
            if (text == null || text.isBlank()) return "";
            StringBuilder sb = new StringBuilder();
            for (String word : text.split("\\s+")) {
                if (!word.isBlank()) {
                    sb.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1).toLowerCase(Locale.ROOT))
                      .append(" ");
                }
            }
            return sb.toString().trim();
        }

        private record GenreMetadata(String id, String name, String tag, String description, String themeClass, String accentColor) {}
    }
}
