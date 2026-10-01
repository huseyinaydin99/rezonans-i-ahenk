package tr.com.huseyinaydin.application.events.models;

import tr.com.huseyinaydin.sharedkernel.events.BaseDomainEvent;
import java.util.UUID;

public class SongPlayedEvent extends BaseDomainEvent {
    private final UUID userId;
    private final UUID songId;

    public SongPlayedEvent(UUID userId, UUID songId) {
        super();
        this.userId = userId;
        this.songId = songId;
    }

    public UUID getUserId() { return userId; }
    public UUID getSongId() { return songId; }
}
