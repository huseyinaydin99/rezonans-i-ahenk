package tr.com.huseyinaydin.infrastructure.messaging.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEvent;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEventDispatcher;

/**
 * Spring In-Memory Event Dispatcher'ın yerini alan ve olayları Kafka'ya fırlatan yeni Dağıtık (Distributed) Yayıncı.
 */
@Component
public class KafkaEventPublisher implements IDomainEventDispatcher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void dispatch(IDomainEvent event) {
        System.out.println("[KAFKA PRODUCER] Olay mesaj kuyruğuna (Broker) fırlatılıyor: " + event.getClass().getSimpleName());
        
        // Olayı, tanımlı Kafka Topic'ine gönderir
        kafkaTemplate.send(KafkaTopicConfig.MUSIC_EVENTS_TOPIC, event.getEventId().toString(), event);
    }
}
