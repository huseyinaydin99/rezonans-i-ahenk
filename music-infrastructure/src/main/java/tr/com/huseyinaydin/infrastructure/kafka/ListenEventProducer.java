package tr.com.huseyinaydin.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ListenEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(ListenEventProducer.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ListenEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendListenEvent(String userId, Long songId) {
        Map<String, Object> event = new HashMap<>();
        event.put("userId", userId);
        event.put("songId", songId);
        event.put("timestamp", System.currentTimeMillis());
        
        try {
            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("song-listen-events", userId, event);
            
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    logger.info("🚀 [KAFKA PRODUCER] Dinleme logu Kafka'ya başarıyla gönderildi: {}", event);
                } else {
                    // DEAD LETTER ASYNC LOGGING (Veri Kaybını Önleme Fallback'i)
                    logger.error("🚨 [KAFKA DEAD-LETTER] Dinleme olayı Kafka'ya iletilemedi! Kayıt ASYNC LOG'a atıldı. Veri: {}", event, ex);
                    // İleride bu veri Filebeat veya Logstash ile taranıp Cassandra'ya manuel gönderilebilir.
                }
            });
        } catch (Exception e) {
            // Senkron (bağlantı oluşturulamadan fırlatılan) hatalar için Fallback
            logger.error("🚨 [KAFKA DEAD-LETTER] Kritik hata! Kafka sunucusuna ulaşılamadı. Kayıt ASYNC LOG'a atıldı. Veri: {}", event, e);
        }
    }
}
