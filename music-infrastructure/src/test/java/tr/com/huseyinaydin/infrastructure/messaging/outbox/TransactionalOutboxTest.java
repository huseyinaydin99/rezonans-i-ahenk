package tr.com.huseyinaydin.infrastructure.messaging.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.application.ports.IOutboxRepository;
import tr.com.huseyinaydin.domain.events.SongCreatedEvent;
import tr.com.huseyinaydin.domain.outbox.DomainEventOutbox;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionalOutboxTest {

    private InMemoryOutboxRepository outboxRepository;
    private ObjectMapper objectMapper;
    private TransactionalOutboxService outboxService;

    @BeforeEach
    void setUp() {
        outboxRepository = new InMemoryOutboxRepository();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        outboxService = new TransactionalOutboxService(outboxRepository, objectMapper);
    }

    @Test
    @DisplayName("TransactionalOutboxService should atomically persist domain event to outbox repository")
    void saveEventToOutbox_shouldPersistEventWithMetadata() {
        UUID songId = UUID.randomUUID();
        UUID artistId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();

        SongCreatedEvent event = new SongCreatedEvent(
            songId,
            "Rezonans Şarkısı",
            artistId,
            "Sanatçı A",
            genreId,
            "https://cdn.example.com/cover.jpg",
            "PREMIUM"
        );

        outboxService.saveEventToOutbox(event);

        List<DomainEventOutbox> unprocessed = outboxRepository.findUnprocessed();
        assertThat(unprocessed).hasSize(1);

        DomainEventOutbox saved = unprocessed.get(0);
        assertThat(saved.getId()).isEqualTo(event.getEventId());
        assertThat(saved.getEventType()).isEqualTo(SongCreatedEvent.class.getName());
        assertThat(saved.getPayload()).contains("Rezonans Şarkısı");
        assertThat(saved.isProcessed()).isFalse();
        assertThat(saved.getRetryCount()).isEqualTo(0);
    }

    @Test
    @DisplayName("OutboxRelayScheduler should safely handle execution when Kafka broker is unreachable without losing data")
    void outboxRelayScheduler_whenKafkaUnavailable_shouldRetainEventAndNotMarkProcessed() {
        UUID eventId = UUID.randomUUID();
        DomainEventOutbox outbox = new DomainEventOutbox(
            eventId,
            "Song",
            eventId.toString(),
            SongCreatedEvent.class.getName(),
            "{\"title\":\"Test\"}",
            Instant.now()
        );
        outboxRepository.save(outbox);

        // Scheduler without active Kafka connection
        OutboxRelayScheduler scheduler = new OutboxRelayScheduler(outboxRepository, null, objectMapper);
        scheduler.processOutboxEvents();

        // Must still be in outbox repository for retry (Zero Data Loss)
        List<DomainEventOutbox> pending = outboxRepository.findUnprocessed();
        assertThat(pending).hasSize(1);
        assertThat(pending.get(0).isProcessed()).isFalse();
    }

    @Test
    @DisplayName("Outbox repository status transitions should correctly mark processed and increment retries")
    void outboxRepository_stateTransitions_shouldWorkCorrectly() {
        UUID eventId = UUID.randomUUID();
        DomainEventOutbox outbox = new DomainEventOutbox(
            eventId,
            "Song",
            eventId.toString(),
            "com.example.TestEvent",
            "{}",
            Instant.now()
        );
        outboxRepository.save(outbox);

        assertThat(outboxRepository.findUnprocessed()).hasSize(1);

        outboxRepository.incrementRetry(eventId, "Connection timeout to Kafka broker");
        assertThat(outbox.getRetryCount()).isEqualTo(1);
        assertThat(outbox.getLastError()).contains("Connection timeout");
        assertThat(outboxRepository.findUnprocessed()).hasSize(1);

        outboxRepository.markProcessed(eventId);
        assertThat(outbox.isProcessed()).isTrue();
        assertThat(outboxRepository.findUnprocessed()).isEmpty();
    }

    private static class InMemoryOutboxRepository implements IOutboxRepository {
        private final Map<UUID, DomainEventOutbox> storage = new LinkedHashMap<>();

        @Override
        public void save(DomainEventOutbox outbox) {
            storage.put(outbox.getId(), outbox);
        }

        @Override
        public List<DomainEventOutbox> findUnprocessed() {
            return findUnprocessed(50);
        }

        @Override
        public List<DomainEventOutbox> findUnprocessed(int batchSize) {
            List<DomainEventOutbox> result = new ArrayList<>();
            for (DomainEventOutbox item : storage.values()) {
                if (!item.isProcessed() && item.getRetryCount() < 5) {
                    result.add(item);
                    if (result.size() >= batchSize) break;
                }
            }
            return result;
        }

        @Override
        public void markProcessed(UUID id) {
            DomainEventOutbox outbox = storage.get(id);
            if (outbox != null) {
                outbox.markProcessed(Instant.now());
            }
        }

        @Override
        public void incrementRetry(UUID id, String error) {
            DomainEventOutbox outbox = storage.get(id);
            if (outbox != null) {
                outbox.incrementRetry(error);
            }
        }
    }
}
