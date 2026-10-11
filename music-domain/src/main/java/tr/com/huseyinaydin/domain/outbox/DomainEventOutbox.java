package tr.com.huseyinaydin.domain.outbox;

import java.time.Instant;
import java.util.UUID;

/**
 * Transactional Outbox entity.
 * Persisted atomically alongside business domain changes in the same database transaction.
 */
public class DomainEventOutbox {
    private UUID id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    private String payload;
    private Instant occurredAt;
    private Instant processedAt;
    private int retryCount;
    private String lastError;

    protected DomainEventOutbox() {
        // JPA
    }

    public DomainEventOutbox(UUID id, String aggregateType, String aggregateId, String eventType, String payload, Instant occurredAt) {
        this.id = id != null ? id : UUID.randomUUID();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.occurredAt = occurredAt != null ? occurredAt : Instant.now();
        this.retryCount = 0;
    }

    public UUID getId() { return id; }
    public String getAggregateType() { return aggregateType; }
    public String getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public Instant getOccurredAt() { return occurredAt; }
    public Instant getProcessedAt() { return processedAt; }
    public int getRetryCount() { return retryCount; }
    public String getLastError() { return lastError; }

    public boolean isProcessed() {
        return this.processedAt != null;
    }

    public boolean isFailed() {
        return this.retryCount >= 5;
    }

    public void markProcessed(Instant processedAt) {
        this.processedAt = processedAt;
    }

    public void incrementRetry(String error) {
        this.retryCount++;
        this.lastError = error;
    }
}
