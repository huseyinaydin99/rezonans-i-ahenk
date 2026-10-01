package tr.com.huseyinaydin.domain.interaction;

import java.util.UUID;
import java.time.LocalDateTime;

// Saf (Pure) Domain Entity
public class SongLike {
    private UUID id;
    private UUID userId;
    private UUID songId;
    private LocalDateTime likedAt;

    protected SongLike() {}

    public SongLike(UUID userId, UUID songId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.songId = songId;
        this.likedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
    public LocalDateTime getLikedAt() { return likedAt; }
}
