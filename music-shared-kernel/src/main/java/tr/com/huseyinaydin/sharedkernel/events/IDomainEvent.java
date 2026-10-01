package tr.com.huseyinaydin.sharedkernel.events;

import java.time.LocalDateTime;
import java.util.UUID;

public interface IDomainEvent {
    UUID getEventId();
    LocalDateTime getOccurredOn();
}
