package tr.com.huseyinaydin.domain.interaction;

import tr.com.huseyinaydin.domain.common.Entity;
import java.time.LocalDateTime;
import java.util.UUID;

public class SongLike extends Entity<UUID> {
    private UUID userId;
    private UUID songId;
    private LocalDateTime likedDate;

    protected SongLike() {
        super();
    }

    public SongLike(UUID userId, UUID songId) {
        super();
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.songId = songId;
        this.likedDate = LocalDateTime.now();
    }

    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
    public LocalDateTime getLikedDate() { return likedDate; }
}
