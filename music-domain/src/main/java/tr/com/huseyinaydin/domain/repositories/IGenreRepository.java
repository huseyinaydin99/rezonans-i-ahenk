package tr.com.huseyinaydin.domain.repositories;

import tr.com.huseyinaydin.domain.genre.Genre;
import java.util.UUID;

public interface IGenreRepository extends IAsyncRepository<Genre, UUID> {
}
