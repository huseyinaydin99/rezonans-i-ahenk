package tr.com.huseyinaydin.web.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import tr.com.huseyinaydin.infrastructure.ElasticSong;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;

@Component
public class ElasticSeeder {
    private static final Logger log = LoggerFactory.getLogger(ElasticSeeder.class);


    @Autowired
    private IElasticSongRepository elasticSongRepository;

    @Autowired(required = false)
    private tr.com.huseyinaydin.application.ports.ISongSearchService songSearchService;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        try {
            if (elasticSongRepository != null && elasticSongRepository.count() == 0) {
                log.info("🌱 Elasticsearch boş, 50+ zengin müzik kataloğu yükleniyor...");

                if (songSearchService != null) {
                    java.util.List<java.util.Map<String, Object>> allSongs = songSearchService.searchSongs(null);
                    for (java.util.Map<String, Object> s : allSongs) {
                        elasticSongRepository.save(createElasticSong(
                            String.valueOf(s.get("id")),
                            String.valueOf(s.get("title")),
                            String.valueOf(s.get("artist")),
                            String.valueOf(s.get("cover")),
                            String.valueOf(s.get("tier"))
                        ));
                    }
                    log.info("✅ Elasticsearch {} adet şarkıyla dolduruldu.", allSongs.size());
                } else {
                    log.info("✅ ISongSearchService bulunamadı, fallback seed atlandı.");
                }
            } else {
                log.info("✅ Elasticsearch zaten dolu, seed atlandı.");
            }
        } catch (Exception e) {
            log.warn("⚠️ Elasticsearch seed işlemi uyarısı: " + e.getMessage());
        }
    }

    private ElasticSong createElasticSong(String id, String title, String artist, String coverUrl, String tier) {
        ElasticSong es = new ElasticSong();
        es.setId(id);
        es.setTitle(title);
        es.setArtist(artist);
        es.setCoverUrl(coverUrl);
        es.setTier(tier);
        return es;
    }
}
