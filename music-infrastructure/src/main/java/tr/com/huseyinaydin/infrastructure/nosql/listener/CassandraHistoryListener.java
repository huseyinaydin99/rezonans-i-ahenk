package tr.com.huseyinaydin.infrastructure.nosql.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.playback.events.SongPlaybackReportedEvent;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;
import tr.com.huseyinaydin.infrastructure.nosql.service.CassandraListeningHistoryService;
import tr.com.huseyinaydin.infrastructure.nosql.table.ListeningHistoryTable;

import java.time.LocalDateTime;

@Component
public class CassandraHistoryListener {

    private final CassandraListeningHistoryService historyService;

    public CassandraHistoryListener(CassandraListeningHistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * CQRS & Event-Driven Mimarisi: Global oynatıcının bildirdiği dinleme olayları
     * Kafka'dan yakalanır ve yalnızca "geçerli dinlenme" eşiğini aşıp sona eren
     * (COMPLETED / SKIPPED) oturumlar Cassandra'ya yazılır.
     * Ara nabızlar (PROGRESS, PAUSED) ve topic'teki diğer olay tipleri yok sayılır.
     */
    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "cassandra-history-group")
    public void consume(Object payload) {
        if (!(payload instanceof SongPlaybackReportedEvent event)) {
            return;
        }
        if (!event.shouldBeRecordedInHistory()) {
            return;
        }

        ListeningHistoryTable history = new ListeningHistoryTable(
                event.getUserId(),
                event.getOccurredOn() != null ? event.getOccurredOn() : LocalDateTime.now(),
                event.getSongId(),
                (int) event.getPositionSeconds()
        );

        historyService.saveHistory(history);
    }
}
