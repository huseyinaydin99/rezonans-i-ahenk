package tr.com.huseyinaydin.sharedkernel.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

/**
 * Base abstract class for all domain events.
 * Implements IDomainEvent for unified event infrastructure support.
 */
public abstract class DomainEvent implements IDomainEvent {
    private final UUID eventId;
    private final Instant occurredAt;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredAt = Instant.now();
    }

    @Override
    public UUID getEventId() { 
        return eventId; 
    }
    
    public Instant getOccurredAt() { 
        return occurredAt; 
    }

    @Override
    public LocalDateTime getOccurredOn() {
        return occurredAt != null ? occurredAt.atZone(ZoneOffset.UTC).toLocalDateTime() : LocalDateTime.now();
    }

    public abstract String getAggregateId();
    public abstract String getAggregateType();
}
