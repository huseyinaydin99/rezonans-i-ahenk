package tr.com.huseyinaydin.domain.history;

import tr.com.huseyinaydin.domain.common.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

public class ListeningHistory extends Entity<UUID> {
    private UUID userId;
    private UUID songId;
    private LocalDateTime listenedAt;

    protected ListeningHistory() {
        super();
    }

    public ListeningHistory(UUID userId, UUID songId) {
        super();
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.songId = songId;
        this.listenedAt = LocalDateTime.now();
    }

    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
    public LocalDateTime getListenedAt() { return listenedAt; }
}
