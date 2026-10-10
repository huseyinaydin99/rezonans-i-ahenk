package tr.com.huseyinaydin.infrastructure.cassandra;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import java.util.UUID;

@Table("listen_history")
public class ListenHistoryEntity {

    @PrimaryKey
    private UUID id;
    
    private String userId;
    private Long songId;
    private Long timestamp;

    public ListenHistoryEntity() {
        this.id = UUID.randomUUID();
    }

    public ListenHistoryEntity(String userId, Long songId, Long timestamp) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.songId = songId;
        this.timestamp = timestamp;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { this.songId = songId; }

    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
