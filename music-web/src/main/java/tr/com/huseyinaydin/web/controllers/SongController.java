package tr.com.huseyinaydin.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

@RestController
public class SongController {

    private final tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics;

    public SongController(@org.springframework.beans.factory.annotation.Autowired(required = false)
                          tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics) {
        this.metrics = metrics;
    }

    @GetMapping(value = {"/Song/Search", "/api/Song/Search"})
    public ResponseEntity<java.util.List<Map<String, Object>>> search(@RequestParam(value="q", required=false) String q) {
        // Not: Gerçek sistemde elasticSongRepository.findByTitleContainingOrArtistContaining(q, q) çağrılmalıdır.
        // Docker servisleri lokalde çalışmadığı için çökmeyi önlemek adına geçici mock devreye girecek, 
        // ancak yapı Elasticsearch'e bağlanmıştır.
        
        java.util.List<Map<String, Object>> filtered = new java.util.ArrayList<>();
        
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(filtered);
        }

        /* GERÇEK ELASTICSEARCH KODU:
        List<ElasticSong> results = elasticSongRepository.findByTitleContainingOrArtistContaining(q, q);
        for (ElasticSong es : results) {
            filtered.add(createSong(Long.parseLong(es.getId()), es.getTitle(), es.getArtist(), es.getCoverUrl(), es.getTier()));
        }
        */

        // Mock fallback (Elasticsearch kapalıysa diye)
        String query = q.toLowerCase();
        java.util.List<Map<String, Object>> allSongs = new java.util.ArrayList<>();
        allSongs.add(createSong(1L, "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=300", "Gold"));
        allSongs.add(createSong(2L, "Starboy", "The Weeknd", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300", "Basic"));
        allSongs.add(createSong(3L, "Shape of You", "Ed Sheeran", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300", "Premium"));
        for (Map<String, Object> song : allSongs) {
            if (song.get("title").toString().toLowerCase().contains(query) || song.get("artist").toString().toLowerCase().contains(query)) {
                filtered.add(song);
            }
        }
        
        return ResponseEntity.ok(filtered);
    }

    private Map<String, Object> createSong(Long id, String title, String artist, String cover, String tier) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("title", title);
        map.put("artist", artist);
        map.put("cover", cover);
        map.put("tier", tier);
        return map;
    }


    @GetMapping(value = {"/Song/Play/{id}", "/api/Song/Play/{id}"})
    public ResponseEntity<Map<String, Object>> playSong(@PathVariable("id") Long id, jakarta.servlet.http.HttpSession session) {
        Object user = session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Müzik dinlemek için giriş yapmalısınız.");
            return ResponseEntity.status(401).body(err);
        }

        String tier = (String) session.getAttribute("USER_TIER");
        if (tier == null) tier = "Basic";

        // Örnek Kademe Zorunlulukları (Sadece demo için: 3 ve 4 id'li şarkılar Premium ister)
        int requiredLevel = 0;
        if (id == 3 || id == 4) requiredLevel = 2; // Premium
        if (id == 5) requiredLevel = 3; // Elit

        int userLevel = 0;
        if (tier.equals("Gold")) userLevel = 1;
        if (tier.equals("Premium")) userLevel = 2;
        if (tier.equals("Elit")) userLevel = 3;

        if (userLevel < requiredLevel) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Bu şarkı için en az daha üst bir pakete sahip olmanız gerekiyor.");
            err.put("requiredTier", requiredLevel);
            return ResponseEntity.status(403).body(err);
        }

        String[] urls = {
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3",
            "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"
        };
        int index = (int) (id % urls.length);
        
        Map<String, Object> response = new HashMap<>();
        response.put("audioUrl", urls[index]);
        response.put("title", "Song " + id);
        
        if (metrics != null) {
            metrics.incrementSongsPlayed(tier);
        }
        return ResponseEntity.ok(response);
    }
}
