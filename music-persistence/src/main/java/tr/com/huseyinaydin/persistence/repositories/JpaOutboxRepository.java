package tr.com.huseyinaydin.persistence.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IOutboxRepository;
import tr.com.huseyinaydin.domain.outbox.DomainEventOutbox;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Enterprise JPA implementation of IOutboxRepository.
 * Manages atomic outbox persistence in the active transaction, and ensures
 * independent status commitments via Propagation.REQUIRES_NEW during relay.
 */
@Repository
public class JpaOutboxRepository implements IOutboxRepository {

    private final EntityManager entityManager;

    public JpaOutboxRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void save(DomainEventOutbox outbox) {
        entityManager.persist(outbox);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEventOutbox> findUnprocessed() {
        return findUnprocessed(50);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEventOutbox> findUnprocessed(int batchSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<DomainEventOutbox> query = cb.createQuery(DomainEventOutbox.class);
        Root<DomainEventOutbox> root = query.from(DomainEventOutbox.class);

        query.where(
                cb.and(
                        cb.isNull(root.get("processedAt")),
                        cb.lessThan(root.get("retryCount"), 5)
                )
        );
        query.orderBy(cb.asc(root.get("occurredAt")));

        return entityManager.createQuery(query)
                .setMaxResults(batchSize > 0 ? batchSize : 50)
                .getResultList();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markProcessed(UUID id) {
        if (id == null) return;
        DomainEventOutbox outbox = entityManager.find(DomainEventOutbox.class, id);
        if (outbox != null) {
            outbox.markProcessed(Instant.now());
            entityManager.merge(outbox);
            entityManager.flush();
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementRetry(UUID id, String error) {
        if (id == null) return;
        DomainEventOutbox outbox = entityManager.find(DomainEventOutbox.class, id);
        if (outbox != null) {
            outbox.incrementRetry(error);
            entityManager.merge(outbox);
            entityManager.flush();
        }
    }
}
