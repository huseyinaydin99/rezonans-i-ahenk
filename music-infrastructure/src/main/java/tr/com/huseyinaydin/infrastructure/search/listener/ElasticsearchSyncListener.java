package tr.com.huseyinaydin.infrastructure.search.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.artist.Artist;
import tr.com.huseyinaydin.domain.events.SongCreatedEvent;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;
import tr.com.huseyinaydin.infrastructure.ElasticSong;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;

import java.util.Map;
import java.util.Optional;

@Component
public class ElasticsearchSyncListener {
    private static final Logger log = LoggerFactory.getLogger(ElasticsearchSyncListener.class);

    private final IElasticSongRepository elasticSongRepository;
    private final IArtistRepository artistRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ElasticsearchSyncListener(IElasticSongRepository elasticSongRepository,
                                     IArtistRepository artistRepository,
                                     @Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate) {
        this.elasticSongRepository = elasticSongRepository;
        this.artistRepository = artistRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * CQRS Gerçek Zamanlı (Real-Time) Senkronizasyon:
     * Command katmanında yeni bir şarkı veritabanına kaydedildiğinde fırlatılan SongCreatedEvent'i
     * anında yakalar ve asenkron olarak Elasticsearch indeksine yazar.
     */
    @EventListener
    @Async
    public void onSongCreated(SongCreatedEvent event) {
        log.info("⚡ [REAL-TIME CQRS SYNC] Yeni şarkı oluşturuldu (Song ID: {}). Elasticsearch endeksine asenkron işleniyor...", event.getSongId());
        
        try {
            String artistName = event.getArtistName();
            if ((artistName == null || artistName.isBlank()) && event.getArtistId() != null) {
                Optional<Artist> artistOpt = artistRepository.findById(event.getArtistId());
                if (artistOpt.isPresent()) {
                    artistName = artistOpt.get().getName();
                }
            }
            if (artistName == null || artistName.isBlank()) {
                artistName = "Bilinmeyen Sanatçı";
            }

            ElasticSong elasticSong = new ElasticSong();
            elasticSong.setId(event.getSongId().toString());
            elasticSong.setTitle(event.getTitle());
            elasticSong.setArtist(artistName);
            elasticSong.setCoverUrl(event.getCoverImageUrl() != null && !event.getCoverImageUrl().isBlank()
                    ? event.getCoverImageUrl()
                    : "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=300");
            elasticSong.setTier(event.getTier() != null ? event.getTier() : "Basic");

            elasticSongRepository.save(elasticSong);
            log.info("✅ [ELASTICSEARCH GÜNCELLENDİ] Şarkı arama motoruna gerçek zamanlı indekslendi: '{}' - '{}' (ID: {})",
                    elasticSong.getTitle(), elasticSong.getArtist(), elasticSong.getId());

            // Dağıtık mikroservis ve replikalar için Kafka yayını Transactional Outbox (OutboxRelayScheduler)
            // tarafından atomik ve güvenilir şekilde (Zero Data Loss) gerçekleştirilmektedir.
            log.info("✅ [CQRS LOCAL ELASTIC SYNC] Şarkı yerel Elasticsearch endeksine işlendi. Kafka yayını Transactional Outbox tarafından garanti edilmektedir.");
        } catch (Exception e) {
            log.error("❌ [ELASTICSEARCH SYNC HATA] Şarkı endekslenirken hata: {}", e.getMessage(), e);
        }
    }

    /**
     * Dağıtık / Broker Tarafı:
     * Kafka üzerinden gelen şarkı olaylarını dinleyerek diğer düğümlerin de senkron kalmasını sağlar.
     */
    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "elastic-sync-group")
    public void syncFromKafka(Object eventPayload) {
        log.info("📥 [KAFKA CONSUMER - ELASTIC SYNC] Kafka'dan şarkı olayı yakalandı, Elasticsearch kontrol ediliyor...");
        try {
            if (eventPayload instanceof SongCreatedEvent event) {
                ElasticSong es = new ElasticSong();
                es.setId(event.getSongId().toString());
                es.setTitle(event.getTitle());
                es.setArtist(event.getArtistName() != null ? event.getArtistName() : "Bilinmeyen Sanatçı");
                es.setCoverUrl(event.getCoverImageUrl());
                es.setTier(event.getTier() != null ? event.getTier() : "Basic");
                elasticSongRepository.save(es);
                log.info("✅ [KAFKA -> ELASTICSEARCH] Kafka üzerinden gelen şarkı Elastic'e yazıldı: {}", es.getTitle());
            } else if (eventPayload instanceof Map<?, ?> rawMap) {
                Object idObj = rawMap.get("songId");
                Object titleObj = rawMap.get("title");
                String id = idObj != null ? idObj.toString() : "";
                String title = titleObj != null ? titleObj.toString() : "";
                if (!id.isBlank() && !title.isBlank()) {
                    ElasticSong es = new ElasticSong();
                    es.setId(id);
                    es.setTitle(title);
                    Object artistObj = rawMap.get("artistName");
                    es.setArtist(artistObj != null ? artistObj.toString() : "Bilinmeyen Sanatçı");
                    Object coverObj = rawMap.get("coverImageUrl");
                    es.setCoverUrl(coverObj != null ? coverObj.toString() : "");
                    Object tierObj = rawMap.get("tier");
                    es.setTier(tierObj != null ? tierObj.toString() : "Basic");
                    elasticSongRepository.save(es);
                    log.info("✅ [KAFKA -> ELASTICSEARCH] Map formatındaki olay Elastic'e yazıldı: {}", title);
                }
            }
        } catch (Exception e) {
            log.error("❌ [KAFKA CONSUME ERROR] Kafka senkronizasyonu hatası: {}", e.getMessage());
        }
    }
}
