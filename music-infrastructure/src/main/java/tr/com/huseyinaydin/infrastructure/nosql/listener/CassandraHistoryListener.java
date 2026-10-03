package tr.com.huseyinaydin.infrastructure.nosql.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.infrastructure.messaging.kafka.config.KafkaTopicConfig;
import tr.com.huseyinaydin.infrastructure.nosql.service.CassandraListeningHistoryService;
import tr.com.huseyinaydin.infrastructure.nosql.table.ListeningHistoryTable;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class CassandraHistoryListener {

    private final CassandraListeningHistoryService historyService;

    public CassandraHistoryListener(CassandraListeningHistoryService historyService) {
        this.historyService = historyService;
    }

    /**
     * CQRS & Event-Driven Mimarisi: Şarkı dinlendiği an Kafka'ya fırlatılan
     * olay havada yakalanır ve asenkron olarak Cassandra'ya yazılır.
     */
    @KafkaListener(topics = KafkaTopicConfig.MUSIC_EVENTS_TOPIC, groupId = "cassandra-history-group")
    public void consumeSongPlayedEvent(Object event) {
        System.out.println("[BIG DATA HATT] Kafka'dan 'Şarkı Dinlendi' olayı yakalandı. Cassandra'ya işleniyor...");
        
        // Simüle edilmiş Kafka mesaj okuması
        ListeningHistoryTable history = new ListeningHistoryTable(
                UUID.randomUUID(), 
                LocalDateTime.now(), 
                UUID.randomUUID(), 
                180
        );
        
        historyService.saveHistory(history);
    }
}
