package tr.com.huseyinaydin.domain.repositories;

import tr.com.huseyinaydin.domain.artist.Artist;
import java.util.UUID;

public interface IArtistRepository extends IAsyncRepository<Artist, UUID> {
}
