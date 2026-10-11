package tr.com.huseyinaydin.domain.repositories;

import tr.com.huseyinaydin.domain.song.read.SongReadModel;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CQRS Read Repository port for high-performance, non-locking song queries.
 * Interacts only with read models and read views, bypassing transactional write tables.
 */
public interface ISongReadRepository {
    Optional<SongReadModel> findById(UUID id);
    List<SongReadModel> findAll(int pageIndex, int pageSize);
    List<SongReadModel> findByGenreId(UUID genreId);
    List<SongReadModel> findByIds(List<UUID> ids);
    long count();
}
