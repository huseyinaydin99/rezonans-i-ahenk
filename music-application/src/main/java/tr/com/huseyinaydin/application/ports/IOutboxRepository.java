package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.domain.outbox.DomainEventOutbox;

import java.util.List;
import java.util.UUID;

/**
 * Port contract for Transactional Outbox persistence.
 */
public interface IOutboxRepository {
    void save(DomainEventOutbox outbox);
    List<DomainEventOutbox> findUnprocessed();
    List<DomainEventOutbox> findUnprocessed(int batchSize);
    void markProcessed(UUID id);
    void incrementRetry(UUID id, String error);
}
