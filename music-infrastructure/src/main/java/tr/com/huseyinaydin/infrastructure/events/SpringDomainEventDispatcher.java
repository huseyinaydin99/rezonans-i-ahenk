package tr.com.huseyinaydin.infrastructure.events;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEvent;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEventDispatcher;

@Component
@org.springframework.context.annotation.Primary
public class SpringDomainEventDispatcher implements IDomainEventDispatcher {
    private static final Logger log = LoggerFactory.getLogger(SpringDomainEventDispatcher.class);


    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventDispatcher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void dispatch(IDomainEvent event) {
        log.info("[EVENT DISPATCHER] Fırlatılıyor: " + event.getClass().getSimpleName());
        applicationEventPublisher.publishEvent(event);
    }
}
