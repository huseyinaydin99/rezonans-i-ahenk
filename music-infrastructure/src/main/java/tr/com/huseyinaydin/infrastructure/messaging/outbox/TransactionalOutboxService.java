package tr.com.huseyinaydin.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IOutboxRepository;
import tr.com.huseyinaydin.domain.outbox.DomainEventOutbox;
import tr.com.huseyinaydin.sharedkernel.events.DomainEvent;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEvent;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Enterprise Transactional Outbox Service.
 * Guarantees atomicity between business database operations and event persistence.
 * Completely eliminates the dual-write problem by saving domain events to the outbox
 * table in the same database transaction.
 */
@Service
public class TransactionalOutboxService {
    private static final Logger log = LoggerFactory.getLogger(TransactionalOutboxService.class);

    private final IOutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public TransactionalOutboxService(IOutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Atomically saves an event to the outbox within the caller's transaction.
     * If the transaction rolls back, the outbox record is also rolled back,
     * ensuring zero phantom messages and zero data loss.
     */
    @Transactional
    public void saveEventToOutbox(IDomainEvent event) {
        if (event == null) {
            log.warn("[TRANSACTIONAL OUTBOX] Null event received, skipping outbox persistence.");
            return;
        }

        try {
            String payloadJson = objectMapper.writeValueAsString(event);
            UUID eventId = event.getEventId() != null ? event.getEventId() : UUID.randomUUID();
            String aggregateType = (event instanceof DomainEvent de && de.getAggregateType() != null)
                    ? de.getAggregateType()
                    : event.getClass().getSimpleName();
            String aggregateId = (event instanceof DomainEvent de && de.getAggregateId() != null)
                    ? de.getAggregateId()
                    : eventId.toString();

            Instant occurredAt;
            if (event instanceof DomainEvent de && de.getOccurredAt() != null) {
                occurredAt = de.getOccurredAt();
            } else if (event.getOccurredOn() != null) {
                occurredAt = event.getOccurredOn().toInstant(ZoneOffset.UTC);
            } else {
                occurredAt = Instant.now();
            }

            DomainEventOutbox outbox = new DomainEventOutbox(
                    eventId,
                    aggregateType,
                    aggregateId,
                    event.getClass().getName(),
                    payloadJson,
                    occurredAt
            );

            outboxRepository.save(outbox);
            log.info("💾 [TRANSACTIONAL OUTBOX] Olay başarıyla Outbox tablosuna yazıldı (Atomic Transaction). Event: '{}', ID: {}",
                    event.getClass().getSimpleName(), eventId);
        } catch (Exception e) {
            log.error("❌ [TRANSACTIONAL OUTBOX ERROR] Olay Outbox tablosuna kaydedilirken hata oluştu: {}", e.getMessage(), e);
            throw new RuntimeException("Transactional Outbox kaydı başarısız oldu", e);
        }
    }
}
