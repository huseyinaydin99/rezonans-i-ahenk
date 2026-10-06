package tr.com.huseyinaydin.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import tr.com.huseyinaydin.infrastructure.ElasticSong;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;

@Component
public class ElasticSeeder {

    @Autowired
    private IElasticSongRepository elasticSongRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        try {
            if (elasticSongRepository.count() == 0) {
                System.out.println("🌱 Elasticsearch boş, örnek müzikler yükleniyor...");

                elasticSongRepository.save(createElasticSong("1", "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1614680376593-902f74cf0d41?w=300", "Gold"));
                elasticSongRepository.save(createElasticSong("2", "Starboy", "The Weeknd", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300", "Basic"));
                elasticSongRepository.save(createElasticSong("3", "Shape of You", "Ed Sheeran", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=300", "Premium"));
                elasticSongRepository.save(createElasticSong("4", "Perfect", "Ed Sheeran", "https://images.unsplash.com/photo-1459749411175-04bf5292ceea?w=300", "Premium"));
                elasticSongRepository.save(createElasticSong("5", "Bohemian Rhapsody", "Queen", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300", "Elit"));
                elasticSongRepository.save(createElasticSong("6", "Hotel California", "Eagles", "https://images.unsplash.com/photo-1493225457224-ca2e325608b4?w=300", "Basic"));

                System.out.println("✅ Elasticsearch örnek verilerle dolduruldu.");
            } else {
                System.out.println("✅ Elasticsearch zaten dolu, seed atlandı.");
            }
        } catch (Exception e) {
            System.err.println("❌ Elasticsearch'e ulaşılamadı veya veri yazılamadı: " + e.getMessage());
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
