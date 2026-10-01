package tr.com.huseyinaydin.persistence.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;
import tr.com.huseyinaydin.domain.genre.Genre;
import tr.com.huseyinaydin.domain.repositories.IGenreRepository;

import java.util.UUID;

@Repository
public class GenreJpaRepository extends JpaRepositoryBase<Genre, UUID> implements IGenreRepository {
    public GenreJpaRepository(EntityManager entityManager) {
        super(entityManager, Genre.class);
    }
}
