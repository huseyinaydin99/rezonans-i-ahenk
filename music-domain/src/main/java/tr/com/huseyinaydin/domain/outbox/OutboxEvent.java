package tr.com.huseyinaydin.domain.outbox;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Outbox Pattern (Giden Kutusu) - Çift Yazma (Dual Write) sorununu çözen çekirdek nesne.
 * Domain olayları Kafka'ya doğrudan atılmak yerine, asıl veritabanı Transaction'ı
 * ile tamamen aynı anda (Atomic) bu tabloya yazılır.
 */
public class OutboxEvent {
    private final UUID id;
    private final String aggregateType;
    private final String aggregateId;
    private final String eventType;
    private final String payloadJson;
    private final LocalDateTime createdAt;
    private boolean processed;

    public OutboxEvent(String aggregateType, String aggregateId, String eventType, String payloadJson) {
        this.id = UUID.randomUUID();
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payloadJson = payloadJson;
        this.createdAt = LocalDateTime.now();
        this.processed = false;
    }

    public UUID getId() { return id; }
    public String getPayloadJson() { return payloadJson; }
    public boolean isProcessed() { return processed; }
    
    public void markAsProcessed() {
        this.processed = true;
    }
}
