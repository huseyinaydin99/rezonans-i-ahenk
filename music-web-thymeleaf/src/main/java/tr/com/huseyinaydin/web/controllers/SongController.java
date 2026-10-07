package tr.com.huseyinaydin.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

import tr.com.huseyinaydin.infrastructure.kafka.ListenEventProducer;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;
import tr.com.huseyinaydin.infrastructure.ElasticSong;

@RestController
public class SongController {

    @Autowired
    private ListenEventProducer listenEventProducer;

    @Autowired
    private IElasticSongRepository elasticSongRepository;

    @GetMapping(value = {"/Song/Search", "/api/Song/Search"})
    public ResponseEntity<List<Map<String, Object>>> search(@RequestParam(value="q", required=false) String q) {
        List<Map<String, Object>> filtered = new java.util.ArrayList<>();
        
        if (q == null || q.trim().isEmpty()) {
            return ResponseEntity.ok(filtered);
        }

        try {
            List<ElasticSong> results = elasticSongRepository.findByTitleContainingOrArtistContaining(q, q);
            for (ElasticSong es : results) {
                filtered.add(createSong(Long.parseLong(es.getId()), es.getTitle(), es.getArtist(), es.getCoverUrl(), es.getTier()));
            }
        } catch (Exception e) {
            // Eğer Elastic kapalıysa veya data yoksa boş dön, ama Exception yutma tam olarak logla
            e.printStackTrace();
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

        // TODO: Gerçek MP3 dosyaları S3 (MinIO) üzerinden veya gerçek bir disk yolundan çekilmeli.
        // Şimdilik test mp3'leri .NET'teki gibi sahte bırakıyoruz çünkü S3'e MP3 yüklemedik.
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
        
        // ML dinleme geçmişi kaydı Kafka'ya gönderiliyor (Asenkron - Çalmayı Yavaşlatmasın)
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            listenEventProducer.sendListenEvent(user.toString(), id);
        });
        
        return ResponseEntity.ok(response);
    }
}
