package tr.com.huseyinaydin.sharedkernel.events;

public interface IDomainEventDispatcher {
    void dispatch(IDomainEvent event);
}
