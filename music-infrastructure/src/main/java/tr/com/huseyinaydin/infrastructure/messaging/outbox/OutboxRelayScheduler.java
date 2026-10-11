package tr.com.huseyinaydin.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IOutboxRepository;
import tr.com.huseyinaydin.domain.outbox.DomainEventOutbox;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Transactional Outbox Relay (Kurye / Dispatcher) servisi.
 * Veritabanındaki 'processed_at IS NULL' olan Outbox olaylarını periyodik olarak okur,
 * Kafka Broker'ına güvenilir biçimde iletir (At-least-once delivery) ve
 * broker onayı (ACK) geldikten sonra veritabanında 'processed_at' olarak işaretler.
 * 
 * Kafka bağlantısı koparsa veya broker yanıt vermezse, mesaj veritabanında kalır
 * ve bir sonraki turda yeniden denenir (Zero Data Loss).
 */
@Component
public class OutboxRelayScheduler {
    private static final Logger log = LoggerFactory.getLogger(OutboxRelayScheduler.class);

    private final IOutboxRepository outboxRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics;

    @Autowired
    public OutboxRelayScheduler(IOutboxRepository outboxRepository,
                                @Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate,
                                ObjectMapper objectMapper,
                                @Autowired(required = false) tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.metrics = metrics;
    }

    public OutboxRelayScheduler(IOutboxRepository outboxRepository,
                                KafkaTemplate<String, Object> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this(outboxRepository, kafkaTemplate, objectMapper, null);
    }

    @Scheduled(fixedDelayString = "${outbox.relay.delay:3000}")
    public void processOutboxEvents() {
        List<DomainEventOutbox> pendingEvents = outboxRepository.findUnprocessed(50);
        if (pendingEvents == null || pendingEvents.isEmpty()) {
            return;
        }

        log.info("🚀 [OUTBOX RELAY] {} adet bekleyen olay bulundu. Kafka'ya güvenle iletiliyor...", pendingEvents.size());

        for (DomainEventOutbox event : pendingEvents) {
            relayEvent(event);
        }
    }

    public boolean relayEvent(DomainEventOutbox event) {
        try {
            if (kafkaTemplate == null) {
                log.warn("⚠️ [OUTBOX RELAY] KafkaTemplate yapılandırılmamış, olay beklemeye alındı: {}", event.getId());
                return false;
            }

            Object payloadObject;
            try {
                Class<?> eventClass = Class.forName(event.getEventType());
                payloadObject = objectMapper.readValue(event.getPayload(), eventClass);
            } catch (Exception e) {
                // If deserialization to original class fails, publish raw JSON string payload
                payloadObject = event.getPayload();
            }

            String topic = KafkaTopicConfig.MUSIC_EVENTS_TOPIC;
            String messageKey = event.getAggregateId() != null ? event.getAggregateId() : event.getId().toString();

            log.info("📡 [OUTBOX RELAY] Olay Kafka'ya aktarılıyor. Tip: {}, ID: {}, Topic: {}",
                    event.getEventType(), event.getId(), topic);

            CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, messageKey, payloadObject);
            
            // Wait synchronously for Kafka Broker ACK (Enforces At-Least-Once Delivery & Zero Data Loss)
            future.get(5, TimeUnit.SECONDS);

            outboxRepository.markProcessed(event.getId());
            if (metrics != null) {
                metrics.incrementKafkaEvent(event.getEventType());
            }
            log.info("✅ [OUTBOX RELAY] Olay başarıyla Kafka'ya teslim edildi ve 'processed' olarak işaretlendi: {}", event.getId());
            return true;

        } catch (Exception e) {
            String errorMsg = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            log.error("❌ [OUTBOX RELAY HATA] Olay Kafka'ya iletilemedi! ID: {}, Hata: {}. Bir sonraki turda yeniden denenecek.",
                    event.getId(), errorMsg);
            try {
                outboxRepository.incrementRetry(event.getId(), errorMsg);
            } catch (Exception retryEx) {
                log.error("❌ [OUTBOX RELAY] Retry sayısı güncellenirken hata: {}", retryEx.getMessage());
            }
            return false;
        }
    }
}
