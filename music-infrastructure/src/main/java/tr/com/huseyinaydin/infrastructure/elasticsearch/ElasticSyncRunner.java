package tr.com.huseyinaydin.infrastructure.elasticsearch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;
import tr.com.huseyinaydin.infrastructure.ElasticSong;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;

import java.util.Arrays;
import java.util.List;

@Component
public class ElasticSyncRunner implements CommandLineRunner {

    private final IElasticSongRepository elasticSongRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    public ElasticSyncRunner(IElasticSongRepository elasticSongRepository, ElasticsearchOperations elasticsearchOperations) {
        this.elasticSongRepository = elasticSongRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Index yoksa oluştur
            IndexCoordinates index = IndexCoordinates.of("songs");
            if (!elasticsearchOperations.indexOps(index).exists()) {
                elasticsearchOperations.indexOps(index).create();
                elasticsearchOperations.indexOps(index).putMapping(ElasticSong.class);
            }

            // Veritabanından Elasticsearch'e senkronizasyon simülasyonu
            long count = elasticSongRepository.count();
            if (count == 0) {
                System.out.println("[ELASTICSEARCH] Index boş, örnek şarkılar senkronize ediliyor...");
                
                List<ElasticSong> seedData = Arrays.asList(
                    createSong("1", "Bohemian Rhapsody", "Queen", "Premium", "https://i.scdn.co/image/ab67616d0000b273e8b066f70c206551210d902b"),
                    createSong("2", "Shape of You", "Ed Sheeran", "Basic", "https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96"),
                    createSong("3", "Blinding Lights", "The Weeknd", "Premium", "https://i.scdn.co/image/ab67616d0000b2738863bc11d2aa12b54f5aeb36"),
                    createSong("4", "Hotel California", "Eagles", "Elit", "https://i.scdn.co/image/ab67616d0000b273462a4d33eb4352fb1fb93e62"),
                    createSong("5", "Stairway to Heaven", "Led Zeppelin", "Elit", "https://i.scdn.co/image/ab67616d0000b273c8a11e48c91a982d086afc69"),
                    createSong("6", "Imagine", "John Lennon", "Basic", "https://i.scdn.co/image/ab67616d0000b273c5c249a1752b77a94464c5d5")
                );

                elasticSongRepository.saveAll(seedData);
                System.out.println("[ELASTICSEARCH] 6 adet şarkı başarıyla indekslendi.");
            } else {
                System.out.println("[ELASTICSEARCH] Indexte halihazırda " + count + " kayıt bulunuyor.");
            }
        } catch (Exception e) {
            System.err.println("[ELASTICSEARCH] Senkronizasyon hatası: " + e.getMessage());
        }
    }

    private ElasticSong createSong(String id, String title, String artist, String tier, String coverUrl) {
        ElasticSong song = new ElasticSong();
        song.setId(id);
        song.setTitle(title);
        song.setArtist(artist);
        song.setTier(tier);
        song.setCoverUrl(coverUrl);
        return song;
    }
}
