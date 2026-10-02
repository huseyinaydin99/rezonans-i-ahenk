package tr.com.huseyinaydin.infrastructure.search.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;
import tr.com.huseyinaydin.infrastructure.search.service.ElasticSongSearchService;
import tr.com.huseyinaydin.infrastructure.search.document.SongDocument;
import java.util.UUID;

@Component
public class ElasticsearchSyncListener {

    private final ElasticSongSearchService searchService;

    public ElasticsearchSyncListener(ElasticSongSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * CQRS Mimarisi: Veritabanına (Command) yazılan veriyi anında yakalayıp 
     * Okuma (Query) motoruna yani Elasticsearch'e yazar.
     */
    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "elastic-sync-group")
    public void syncToElasticsearch(Object event) {
        System.out.println("[SYNC HATT] Kafka'dan gelen veri yakalandı, Elasticsearch indeksleri güncelleniyor...");
        
        // Event nesnesinden şarkı bilgileri çıkarılır (Mocking logic for simulation)
        SongDocument newDoc = new SongDocument(UUID.randomUUID().toString(), "Yeni Yüklenen Şarkı", "Bilinmeyen Sanatçı", "Genel");
        searchService.indexNewSong(newDoc);
    }
}
