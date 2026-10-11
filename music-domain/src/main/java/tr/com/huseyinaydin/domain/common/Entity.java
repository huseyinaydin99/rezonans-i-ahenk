package tr.com.huseyinaydin.domain.common;

import tr.com.huseyinaydin.sharedkernel.events.DomainEvent;

public abstract class Entity<TId> extends BaseEntity<TId> {

    protected Entity() {
        super();
    }

    protected void addDomainEvent(DomainEvent event) {
        registerEvent(event);
    }
}
