package tr.com.huseyinaydin.domain.events;

import tr.com.huseyinaydin.domain.enums.UserRole;
import tr.com.huseyinaydin.sharedkernel.events.DomainEvent;

import java.util.UUID;

public class UserRegisteredEvent extends DomainEvent {
    private final UUID userId;
    private final UUID customerId;
    private final String email;
    private final UserRole role;

    protected UserRegisteredEvent() {
        super();
        this.userId = null;
        this.customerId = null;
        this.email = null;
        this.role = null;
    }

    public UserRegisteredEvent(UUID userId, UUID customerId, String email, UserRole role) {
        super();
        this.userId = userId;
        this.customerId = customerId;
        this.email = email;
        this.role = role;
    }

    @Override
    public String getAggregateId() {
        return userId != null ? userId.toString() : "";
    }

    @Override
    public String getAggregateType() {
        return "ApplicationUser";
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }
}
