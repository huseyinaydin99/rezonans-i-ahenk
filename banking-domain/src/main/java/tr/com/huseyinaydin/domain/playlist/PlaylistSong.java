package tr.com.huseyinaydin.domain.playlist;

import tr.com.huseyinaydin.domain.common.BaseEntity;
import java.time.LocalDateTime;
import java.util.UUID;

public class PlaylistSong extends BaseEntity<UUID> {
    private UUID playlistId;
    private UUID songId;
    private LocalDateTime addedDate;

    protected PlaylistSong() {
        super();
    }

    public PlaylistSong(UUID playlistId, UUID songId) {
        super();
        this.id = UUID.randomUUID();
        this.playlistId = playlistId;
        this.songId = songId;
        this.addedDate = LocalDateTime.now();
    }

    public UUID getPlaylistId() { return playlistId; }
    public UUID getSongId() { return songId; }
    public LocalDateTime getAddedDate() { return addedDate; }
}
