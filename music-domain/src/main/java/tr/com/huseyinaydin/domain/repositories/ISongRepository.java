package tr.com.huseyinaydin.domain.repositories;

import tr.com.huseyinaydin.domain.song.Song;
import java.util.UUID;

public interface ISongRepository extends IAsyncRepository<Song, UUID> {
}
