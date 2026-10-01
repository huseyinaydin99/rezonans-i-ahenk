package tr.com.huseyinaydin.persistence.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import tr.com.huseyinaydin.domain.playlist.Playlist;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;

import java.util.UUID;

@Repository
public class PlaylistJpaRepository extends JpaRepositoryBase<Playlist, UUID> implements IPlaylistRepository {
    public PlaylistJpaRepository(EntityManager entityManager) {
        super(entityManager, Playlist.class);
    }
}
