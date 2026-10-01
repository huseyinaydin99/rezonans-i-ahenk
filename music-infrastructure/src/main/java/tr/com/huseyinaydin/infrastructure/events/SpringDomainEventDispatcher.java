package tr.com.huseyinaydin.infrastructure.events;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEvent;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEventDispatcher;

@Component
public class SpringDomainEventDispatcher implements IDomainEventDispatcher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventDispatcher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void dispatch(IDomainEvent event) {
        System.out.println("[EVENT DISPATCHER] Fırlatılıyor: " + event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}
