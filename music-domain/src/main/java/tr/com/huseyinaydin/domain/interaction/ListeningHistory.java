package tr.com.huseyinaydin.domain.interaction;

import java.util.UUID;
import java.time.LocalDateTime;

// Saf (Pure) Domain Entity
public class ListeningHistory {
    private UUID id;
    private UUID userId;
    private UUID songId;
    private LocalDateTime listenedAt;
    private long listenDurationSeconds;

    protected ListeningHistory() {}

    public ListeningHistory(UUID userId, UUID songId, long listenDurationSeconds) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.songId = songId;
        this.listenDurationSeconds = listenDurationSeconds;
        this.listenedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
    public LocalDateTime getListenedAt() { return listenedAt; }
    public long getListenDurationSeconds() { return listenDurationSeconds; }
}
