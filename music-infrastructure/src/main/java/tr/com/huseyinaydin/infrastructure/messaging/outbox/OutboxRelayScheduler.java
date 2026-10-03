package tr.com.huseyinaydin.infrastructure.messaging.outbox;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;
import tr.com.huseyinaydin.domain.outbox.OutboxEvent;
import java.util.Collections;
import java.util.List;

/**
 * Arka planda sürekli (Örn: Her 5 saniyede bir) çalışan kurye servisi.
 * Veritabanındaki 'processed = false' olan Outbox olaylarını toplar
 * ve Kafka'ya fırlatır (At-least-once delivery).
 */
@Component
public class OutboxRelayScheduler {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OutboxRelayScheduler(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 5000)
    public void processOutboxEvents() {
        // Gerçekte veritabanından 'WHERE processed = false' sorgusu atılır
        List<OutboxEvent> pendingEvents = fetchUnprocessedEvents();

        for (OutboxEvent event : pendingEvents) {
            System.out.println("[OUTBOX KURYR (RELAY)] Bekleyen olay Kafka Broker'ına güvenle fırlatılıyor...");
            
            // 1. Olayı Kafka'ya fırlat
            kafkaTemplate.send(KafkaTopicConfig.MUSIC_EVENTS_TOPIC, event.getId().toString(), event.getPayloadJson());
            
            // 2. İşlendi olarak işaretle ve veritabanında güncelle
            event.markAsProcessed();
            System.out.println("[OUTBOX KURYR (RELAY)] Olay işlendi ve veritabanında 'processed=true' olarak işaretlendi.");
        }
    }
    
    private List<OutboxEvent> fetchUnprocessedEvents() {
        // Simülasyon: Hiç bekleyen olay yokmuş gibi dönüyoruz, log kalabalığı yapmasın.
        return Collections.emptyList(); 
    }
}
