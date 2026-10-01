package tr.com.huseyinaydin.persistence.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;

import java.util.UUID;

@Repository
public class SongJpaRepository extends JpaRepositoryBase<Song, UUID> implements ISongRepository {
    public SongJpaRepository(EntityManager entityManager) {
        super(entityManager, Song.class);
    }
}
