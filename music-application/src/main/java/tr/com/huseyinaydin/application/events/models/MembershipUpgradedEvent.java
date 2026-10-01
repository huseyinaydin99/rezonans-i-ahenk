package tr.com.huseyinaydin.application.events.models;

import tr.com.huseyinaydin.sharedkernel.events.BaseDomainEvent;
import java.util.UUID;

public class MembershipUpgradedEvent extends BaseDomainEvent {
    private final UUID userId;
    private final String oldTier;
    private final String newTier;

    public MembershipUpgradedEvent(UUID userId, String oldTier, String newTier) {
        super();
        this.userId = userId;
        this.oldTier = oldTier;
        this.newTier = newTier;
    }

    public UUID getUserId() { return userId; }
    public String getOldTier() { return oldTier; }
    public String getNewTier() { return newTier; }
}
