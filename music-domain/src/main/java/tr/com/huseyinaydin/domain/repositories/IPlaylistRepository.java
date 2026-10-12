package tr.com.huseyinaydin.domain.repositories;

import tr.com.huseyinaydin.domain.playlist.Playlist;
import java.util.List;
import java.util.UUID;

public interface IPlaylistRepository extends IAsyncRepository<Playlist, UUID> {
    List<Playlist> findByUserId(UUID userId);
}
