package tr.com.huseyinaydin.persistence.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import tr.com.huseyinaydin.domain.artist.Artist;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;

import java.util.UUID;

@Repository
public class ArtistJpaRepository extends JpaRepositoryBase<Artist, UUID> implements IArtistRepository {
    public ArtistJpaRepository(EntityManager entityManager) {
        super(entityManager, Artist.class);
    }
}
