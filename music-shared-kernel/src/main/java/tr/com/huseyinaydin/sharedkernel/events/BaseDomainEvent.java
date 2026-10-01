package tr.com.huseyinaydin.sharedkernel.events;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseDomainEvent implements IDomainEvent {
    private final UUID eventId;
    private final LocalDateTime occurredOn;

    protected BaseDomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public UUID getEventId() { return eventId; }
    
    @Override
    public LocalDateTime getOccurredOn() { return occurredOn; }
}
