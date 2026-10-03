package tr.com.huseyinaydin.infrastructure.nosql.table;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Cassandra'nın sütun ailesi (Column Family) yapısına uygun, 
 * kullanıcı bazlı hızlı okuma (Clustering Key) için optimize edilmiş veri modeli.
 */
@Table("listening_histories")
public class ListeningHistoryTable {

    // Partition Key: Verilerin kullanıcı ID'sine göre aynı Node'a düşmesini sağlar
    @PrimaryKeyColumn(name = "user_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private UUID userId;

    // Clustering Key: Verilerin zaman damgasına göre azalan şekilde (DESC) dizilmesini sağlar
    @PrimaryKeyColumn(name = "listened_at", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private LocalDateTime listenedAt;

    @Column("song_id")
    private UUID songId;

    @Column("duration_seconds")
    private int durationSeconds;

    public ListeningHistoryTable(UUID userId, LocalDateTime listenedAt, UUID songId, int durationSeconds) {
        this.userId = userId;
        this.listenedAt = listenedAt;
        this.songId = songId;
        this.durationSeconds = durationSeconds;
    }
}
