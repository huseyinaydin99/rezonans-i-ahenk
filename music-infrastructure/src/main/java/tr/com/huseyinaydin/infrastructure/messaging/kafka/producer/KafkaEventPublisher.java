package tr.com.huseyinaydin.infrastructure.messaging.kafka.producer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(KafkaEventPublisher.class);


    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics;

    public KafkaEventPublisher(KafkaTemplate<String, Object> kafkaTemplate,
                               @org.springframework.beans.factory.annotation.Autowired(required = false) tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics) {
        this.kafkaTemplate = kafkaTemplate;
        this.metrics = metrics;
    }

    @Override
    public void dispatch(IDomainEvent event) {
        String eventName = event.getClass().getSimpleName();
        log.info("[KAFKA PRODUCER] Olay mesaj kuyruğuna (Broker) fırlatılıyor: {}", eventName);
        if (metrics != null) {
            metrics.incrementKafkaEvent(eventName);
        }
        kafkaTemplate.send(KafkaTopicConfig.MUSIC_EVENTS_TOPIC, event.getEventId().toString(), event);
    }
}
