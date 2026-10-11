package tr.com.huseyinaydin.infrastructure.adapters;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;
import tr.com.huseyinaydin.infrastructure.ElasticSong;

import java.util.*;

@Service
public class SongSearchAdapter implements ISongSearchService {
    private static final Logger log = LoggerFactory.getLogger(SongSearchAdapter.class);

    private final IElasticSongRepository elasticSongRepository;

    // Resilient Enterprise Catalog: Şarkı, sanatçı ve tür (genre) bilgilerini içeren platform şarkı kataloğu.
    // Elasticsearch erişilemez veya indeks boş olsa dahi sistemin kesintisiz çalışmasını garanti eder.
    private static final List<CatalogSong> CATALOG = List.of(
        new CatalogSong(1L, "Blinding Lights", "The Weeknd", "Pop / Synthwave", "Gold", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=300"),
        new CatalogSong(2L, "Starboy", "The Weeknd", "Pop / R&B", "Basic", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300"),
        new CatalogSong(3L, "Shape of You", "Ed Sheeran", "Pop", "Premium", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300"),
        new CatalogSong(4L, "Perfect", "Ed Sheeran", "Pop / Akustik", "Premium", "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=300"),
        new CatalogSong(5L, "Bohemian Rhapsody", "Queen", "Rock / Classic", "Elit", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300"),
        new CatalogSong(6L, "Hotel California", "Eagles", "Rock / Classic", "Basic", "https://images.unsplash.com/photo-1493225457224-ca2e325608b4?w=300"),
        new CatalogSong(7L, "Stairway To Heaven", "Led Zeppelin", "Rock", "Basic", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80"),
        new CatalogSong(8L, "Vampire", "Olivia Rodrigo", "Pop / Rock", "Premium", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?auto=format&fit=crop&w=400&q=80"),
        new CatalogSong(9L, "Paint The Town Red", "Doja Cat", "Hip-Hop / Rap", "Elit", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80")
    );

    public SongSearchAdapter(@Autowired(required = false) IElasticSongRepository elasticSongRepository) {
        this.elasticSongRepository = elasticSongRepository;
    }

    @Override
    public List<Map<String, Object>> searchSongs(String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return results;
        }

        String term = keyword.trim().toLowerCase(Locale.ROOT);
        Set<String> seenIds = new HashSet<>();

        // 1. Birincil: Elasticsearch Full-Text Arama
        if (elasticSongRepository != null) {
            try {
                List<ElasticSong> elasticSongs = elasticSongRepository.findByTitleContainingOrArtistContaining(keyword.trim(), keyword.trim());
                if (elasticSongs != null) {
                    for (ElasticSong es : elasticSongs) {
                        Map<String, Object> map = new HashMap<>();
                        Object idVal = es.getId();
                        try {
                            idVal = Long.parseLong(es.getId());
                        } catch (NumberFormatException ignored) {}
                        map.put("id", idVal);
                        map.put("title", es.getTitle());
                        map.put("artist", es.getArtist());
                        map.put("cover", es.getCoverUrl());
                        map.put("tier", es.getTier());
                        results.add(map);
                        seenIds.add(String.valueOf(idVal));
                    }
                }
            } catch (Exception e) {
                log.warn("[ELASTICSEARCH] Full-Text arama esnasında bağlantı/sorgu uyarısı (Fallback devrede): {}", e.getMessage());
            }
        }

        // 2. İkincil / Tamamlayıcı: Katalog ve Tür (Genre) / Sanatçı / Şarkı Eşleşmesi
        // Elasticsearch kapalı olsa, boş olsa veya kullanıcı bir "Tür" (örn: Pop, Rock, Hip-Hop) arasa dahi kesintisiz sonuç üretir.
        for (CatalogSong cs : CATALOG) {
            if (seenIds.contains(String.valueOf(cs.id()))) {
                continue;
            }
            boolean matchTitle = cs.title().toLowerCase(Locale.ROOT).contains(term);
            boolean matchArtist = cs.artist().toLowerCase(Locale.ROOT).contains(term);
            boolean matchGenre = cs.genre().toLowerCase(Locale.ROOT).contains(term);

            if (matchTitle || matchArtist || matchGenre) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cs.id());
                map.put("title", cs.title());
                map.put("artist", cs.artist());
                map.put("genre", cs.genre());
                map.put("cover", cs.coverUrl());
                map.put("tier", cs.tier());
                results.add(map);
                seenIds.add(String.valueOf(cs.id()));
            }
        }

        return results;
    }

    private record CatalogSong(Long id, String title, String artist, String genre, String tier, String coverUrl) {}
}
