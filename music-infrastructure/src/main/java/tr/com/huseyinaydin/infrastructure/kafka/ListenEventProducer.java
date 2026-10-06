package tr.com.huseyinaydin.infrastructure.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class ListenEventProducer {

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
            kafkaTemplate.send("song-listen-events", userId, event);
            System.out.println("🚀 [KAFKA PRODUCER] Dinleme logu Kafka'ya fırlatıldı: " + event);
        } catch (Exception e) {
            System.err.println("Kafka sunucusu kapalı olduğu için olay gönderilemedi (Demo Fallback).");
        }
    }
}
