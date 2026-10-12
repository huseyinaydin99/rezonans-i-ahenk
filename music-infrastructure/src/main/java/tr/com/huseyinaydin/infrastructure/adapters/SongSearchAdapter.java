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
        // Depeche Mode (Elektronik / Rock / Synthwave)
        new CatalogSong(1L, "People Are People", "Depeche Mode", "Elektronik", "Premium", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "3:45"),
        new CatalogSong(2L, "Just Can't Get Enough", "Depeche Mode", "Synthwave", "Premium", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "3:41"),
        new CatalogSong(3L, "Never Let Me Down Again", "Depeche Mode", "Rock", "Premium", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "4:47"),
        new CatalogSong(4L, "Policy of Truth", "Depeche Mode", "Elektronik", "Premium", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "4:55"),
        new CatalogSong(5L, "Personal Jesus", "Depeche Mode", "Rock", "Premium", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?w=400&q=80", "3:44"),
        new CatalogSong(6L, "Enjoy the Silence", "Depeche Mode", "Elektronik", "Elit", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "4:15"),
        new CatalogSong(7L, "Strangelove", "Depeche Mode", "Elektronik", "Gold", "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400&q=80", "3:45"),
        new CatalogSong(8L, "Walking in My Shoes", "Depeche Mode", "Rock", "Premium", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "5:02"),
        new CatalogSong(9L, "Precious", "Depeche Mode", "Rock", "Basic", "https://images.unsplash.com/photo-1465847899084-d164df4dedc6?w=400&q=80", "4:10"),
        new CatalogSong(10L, "Master and Servant", "Depeche Mode", "Elektronik", "Gold", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "3:48"),

        // The Midnight & Nikki Flores (Synthwave)
        new CatalogSong(11L, "Light Years", "The Midnight & Nikki Flores", "Synthwave", "Premium", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "3:32"),
        new CatalogSong(12L, "River of Darkness (feat. Timecop1983)", "The Midnight", "Synthwave", "Premium", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "6:05"),
        new CatalogSong(13L, "Midnight (I Want You in My Arms)", "Lachi James", "Synthwave", "Premium", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "3:42"),
        new CatalogSong(14L, "Dance The Night", "Dua Lipa", "Pop", "Basic", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "2:56"),
        new CatalogSong(15L, "Houdini", "Dua Lipa", "Pop", "Premium", "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80", "3:05"),

        // FM-84, Ollie Wride & Josh Dally (Synthwave)
        new CatalogSong(16L, "Running in the Night", "FM-84 & Ollie Wride", "Synthwave", "Premium", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "4:30"),
        new CatalogSong(17L, "Never Stop", "FM-84", "Synthwave", "Gold", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "4:48"),
        new CatalogSong(18L, "Wild Ones", "FM-84, Timecop1983 & Josh Dally", "Synthwave", "Elit", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "4:28"),
        new CatalogSong(19L, "Bend & Break", "FM-84 & Ollie Wride", "Synthwave", "Basic", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "4:36"),
        new CatalogSong(20L, "Atlas", "FM-84", "Synthwave", "Premium", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "5:51"),

        // Kavinsky & Angèle & Phoenix (Synthwave / Elektronik)
        new CatalogSong(21L, "Nightcall", "Kavinsky", "Synthwave", "Premium", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "4:19"),
        new CatalogSong(22L, "Roadgame", "Kavinsky", "Elektronik", "Gold", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "3:49"),
        new CatalogSong(23L, "Cameo", "Kavinsky, Angèle & Phoenix", "Elektronik", "Premium", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "4:18"),
        new CatalogSong(24L, "Renegade", "Kavinsky", "Elektronik", "Elit", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "3:17"),
        new CatalogSong(25L, "Pacific Coast Highway", "Kavinsky", "Synthwave", "Basic", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "3:55"),

        // Lazerhawk (Synthwave)
        new CatalogSong(26L, "Overdrive", "Lazerhawk", "Synthwave", "Premium", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "4:31"),
        new CatalogSong(27L, "So Far Away", "Lazerhawk", "Synthwave", "Gold", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "4:10"),
        new CatalogSong(28L, "King of the Streets", "Lazerhawk", "Synthwave", "Elit", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "4:40"),
        new CatalogSong(29L, "Electric Groove", "Lazerhawk", "Synthwave", "Basic", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "4:45"),

        // The Midnight (Synthwave koleksiyonu)
        new CatalogSong(30L, "Sunset", "The Midnight", "Synthwave", "Gold", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "5:26"),
        new CatalogSong(31L, "Days of Thunder", "The Midnight", "Synthwave", "Basic", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "5:24"),
        new CatalogSong(32L, "Gloria", "The Midnight", "Synthwave", "Elit", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "6:02"),
        new CatalogSong(33L, "Los Angeles", "The Midnight", "Synthwave", "Premium", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "6:30"),
        new CatalogSong(34L, "Jason", "The Midnight & Nikki Flores", "Synthwave", "Gold", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "5:31"),
        new CatalogSong(35L, "Vampires", "The Midnight", "Synthwave", "Basic", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "5:18"),

        // Lachi James (Synthwave)
        new CatalogSong(36L, "Neon Horizon", "Lachi James", "Synthwave", "Gold", "https://images.unsplash.com/photo-1550684848-fac1c5b4e853?w=400&q=80", "4:12"),
        new CatalogSong(37L, "Retrograde", "Lachi James", "Synthwave", "Basic", "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&q=80", "3:55"),
        new CatalogSong(38L, "City Lights", "Lachi James", "Synthwave", "Premium", "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?w=400&q=80", "4:05"),

        // Dua Lipa (Pop)
        new CatalogSong(39L, "Levitating", "Dua Lipa", "Pop", "Gold", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "3:23"),
        new CatalogSong(40L, "Don't Start Now", "Dua Lipa", "Pop", "Premium", "https://images.unsplash.com/photo-1520523839898-507121c21528?w=400&q=80", "3:03"),
        new CatalogSong(41L, "Physical", "Dua Lipa", "Pop", "Elit", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?w=400&q=80", "3:13"),
        new CatalogSong(42L, "Break My Heart", "Dua Lipa", "Pop", "Basic", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "3:41"),
        new CatalogSong(43L, "Training Season", "Dua Lipa", "Pop", "Basic", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "3:29"),

        // Pop & Rock Klasikleri
        new CatalogSong(44L, "Blinding Lights", "The Weeknd", "Pop", "Gold", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=400&q=80", "3:20"),
        new CatalogSong(45L, "Starboy", "The Weeknd", "Pop", "Basic", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=400&q=80", "3:50"),
        new CatalogSong(46L, "Shape of You", "Ed Sheeran", "Pop", "Premium", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=400&q=80", "3:53"),
        new CatalogSong(47L, "Perfect", "Ed Sheeran", "Pop", "Premium", "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=400&q=80", "4:23"),
        new CatalogSong(48L, "Bohemian Rhapsody", "Queen", "Rock", "Elit", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "5:55"),
        new CatalogSong(49L, "Hotel California", "Eagles", "Rock", "Basic", "https://images.unsplash.com/photo-1493225457224-ca2e325608b4?w=400&q=80", "6:30"),
        new CatalogSong(50L, "Stairway To Heaven", "Led Zeppelin", "Rock", "Basic", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=400&q=80", "8:02"),

        // Sanatçılar & Caz
        new CatalogSong(51L, "Midnight Sax", "Zee-ker", "Jazz", "Gold", "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=400&q=80", "4:15"),
        new CatalogSong(52L, "Cyber Lotus", "Serene Serene", "Synthwave", "Premium", "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=400&q=80", "3:50"),
        new CatalogSong(53L, "Cosmic Resonance", "Royalty Free Music Background", "Elektronik", "Basic", "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&q=80", "3:15"),
        new CatalogSong(54L, "Take Five", "Dave Brubeck", "Jazz", "Basic", "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=400&q=80", "5:24"),
        new CatalogSong(55L, "Autumn Leaves", "Miles Davis", "Jazz", "Premium", "https://images.unsplash.com/photo-1511192336575-5a79af67a629?w=400&q=80", "10:59")
    );

    public SongSearchAdapter(@Autowired(required = false) IElasticSongRepository elasticSongRepository) {
        this.elasticSongRepository = elasticSongRepository;
    }

    @Override
    public List<Map<String, Object>> searchSongs(String keyword) {
        List<Map<String, Object>> results = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            // Arama terimi boşsa tüm kataloğu dön
            for (CatalogSong cs : CATALOG) {
                results.add(toMap(cs));
            }
            return results;
        }

        String term = keyword.trim().toLowerCase(Locale.ROOT);
        String cleanTerm = term.startsWith("#") ? term.substring(1).trim() : term;
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
                        String g = "Pop";
                        for (CatalogSong cs : CATALOG) {
                            if (String.valueOf(cs.id()).equals(String.valueOf(idVal))) {
                                g = cs.genre();
                                break;
                            }
                        }
                        map.put("genre", g);
                        map.put("tier", es.getTier());
                        map.put("duration", "3:30");
                        results.add(map);
                        seenIds.add(String.valueOf(idVal));
                    }
                }
            } catch (Exception e) {
                log.warn("[ELASTICSEARCH] Full-Text arama esnasında bağlantı/sorgu uyarısı (Fallback devrede): {}", e.getMessage());
            }
        }

        // 2. İkincil / Tamamlayıcı: Genişletilmiş Katalog ve Tür (Genre) / Sanatçı / Şarkı Eşleşmesi
        for (CatalogSong cs : CATALOG) {
            if (seenIds.contains(String.valueOf(cs.id()))) {
                continue;
            }
            String genreLower = cs.genre().toLowerCase(Locale.ROOT);
            boolean matchTitle = cs.title().toLowerCase(Locale.ROOT).contains(term);
            boolean matchArtist = cs.artist().toLowerCase(Locale.ROOT).contains(term);
            boolean matchGenre = genreLower.contains(cleanTerm) || genreLower.contains(term) || ("#" + genreLower).contains(term);

            if (matchTitle || matchArtist || matchGenre) {
                results.add(toMap(cs));
                seenIds.add(String.valueOf(cs.id()));
            }
        }

        return results;
    }

    private Map<String, Object> toMap(CatalogSong cs) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", cs.id());
        map.put("title", cs.title());
        map.put("artist", cs.artist());
        map.put("genre", cs.genre());
        map.put("cover", cs.coverUrl());
        map.put("tier", cs.tier());
        map.put("duration", cs.duration());
        return map;
    }

    @Override
    public Map<String, Object> getSongById(Long songId) {
        if (songId == null) return null;
        for (CatalogSong cs : CATALOG) {
            if (cs.id().equals(songId)) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cs.id());
                map.put("title", cs.title());
                map.put("artist", cs.artist());
                map.put("genre", cs.genre());
                map.put("cover", cs.coverUrl());
                map.put("tier", cs.tier());
                map.put("duration", cs.duration());
                return map;
            }
        }
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("id", songId);
        fallback.put("title", "Şarkı #" + songId);
        fallback.put("artist", "Sanatçı");
        fallback.put("genre", "Pop");
        fallback.put("cover", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300");
        fallback.put("tier", "Basic");
        fallback.put("duration", "3:30");
        return fallback;
    }

    @Override
    public List<Map<String, Object>> getSongsByIds(List<Long> songIds) {
        List<Map<String, Object>> list = new ArrayList<>();
        if (songIds == null || songIds.isEmpty()) return list;
        for (Long id : songIds) {
            Map<String, Object> song = getSongById(id);
            if (song != null) {
                list.add(song);
            }
        }
        return list;
    }

    @Override
    public Map<String, Integer> getGenreSongCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        counts.put("Pop", 0);
        counts.put("Rock", 0);
        counts.put("Jazz", 0);
        counts.put("Elektronik", 0);
        counts.put("Synthwave", 0);

        for (CatalogSong cs : CATALOG) {
            String g = cs.genre();
            if (counts.containsKey(g)) {
                counts.put(g, counts.get(g) + 1);
            }
        }
        return counts;
    }

    private record CatalogSong(Long id, String title, String artist, String genre, String tier, String coverUrl, String duration) {}
}
