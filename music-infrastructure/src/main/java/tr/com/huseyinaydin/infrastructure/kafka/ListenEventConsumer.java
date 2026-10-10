package tr.com.huseyinaydin.infrastructure.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.huseyinaydin.infrastructure.cassandra.IListenHistoryCassandraRepository;
import tr.com.huseyinaydin.infrastructure.cassandra.ListenHistoryEntity;
import java.util.Map;

@Service
public class ListenEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ListenEventConsumer.class);
    
    // Uygulama contextinde Cassandra bean'i yüklü olmadığında çökmemesi için @Autowired (required=false) 
    // veya try-catch yapısı kullanılabilir. Projede Cassandra aktifse enjekte edilir.
    private final IListenHistoryCassandraRepository cassandraRepository;

    public ListenEventConsumer(IListenHistoryCassandraRepository cassandraRepository) {
        this.cassandraRepository = cassandraRepository;
    }

    @KafkaListener(topics = "song-listen-events", groupId = "analytics-group")
    public void consume(Map<String, Object> event) {
        try {
            String userId = (String) event.get("userId");
            // Map'ten gelen sayısal değerler Integer veya Long olabilir
            Long songId = Long.valueOf(event.get("songId").toString());
            Long timestamp = Long.valueOf(event.get("timestamp").toString());

            ListenHistoryEntity history = new ListenHistoryEntity(userId, songId, timestamp);
            
            // Veriyi Cassandra'ya kaydet (Spark için Big Data Data Pipeline)
            cassandraRepository.save(history);
            
            logger.info("✅ [DATA PIPELINE] Dinleme logu Cassandra Analytics tablosuna yazıldı: User={}, Song={}", userId, songId);
        } catch (Exception e) {
            logger.error("❌ [DATA PIPELINE] Kafka mesajı işlenirken hata oluştu (Cassandra bağlantısı veya format hatası): {}", e.getMessage());
        }
    }
}
