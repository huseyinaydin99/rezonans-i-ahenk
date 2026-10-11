package tr.com.huseyinaydin.persistence.repositories.read;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * CQRS Read Repository implementation.
 * Operates strictly with @Transactional(readOnly = true) and Hibernate read-only hints
 * against the read-optimized v_song_catalog view / SongReadModel, preventing lock contention
 * on transactional tables and eliminating Hibernate dirty checking overhead.
 */
@Repository
@Transactional(readOnly = true)
public class SongReadRepositoryImpl implements ISongReadRepository {

    private static final Logger log = LoggerFactory.getLogger(SongReadRepositoryImpl.class);

    private final EntityManager entityManager;

    public SongReadRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<SongReadModel> findById(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        try {
            TypedQuery<SongReadModel> query = entityManager.createQuery(
                "SELECT s FROM SongReadModel s WHERE s.id = :id", SongReadModel.class);
            query.setParameter("id", id);
            query.setHint("org.hibernate.readOnly", true);
            return query.getResultStream().findFirst();
        } catch (Exception e) {
            log.warn("Error reading song by id {} from read model: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<SongReadModel> findAll(int pageIndex, int pageSize) {
        try {
            TypedQuery<SongReadModel> query = entityManager.createQuery(
                "SELECT s FROM SongReadModel s ORDER BY s.createdDate DESC", SongReadModel.class);
            query.setHint("org.hibernate.readOnly", true);
            query.setFirstResult(pageIndex * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (Exception e) {
            log.warn("Error reading songs page from read model: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<SongReadModel> findByGenreId(UUID genreId) {
        if (genreId == null) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<SongReadModel> query = entityManager.createQuery(
                "SELECT s FROM SongReadModel s WHERE s.genreId = :genreId ORDER BY s.playCount DESC", SongReadModel.class);
            query.setParameter("genreId", genreId);
            query.setHint("org.hibernate.readOnly", true);
            return query.getResultList();
        } catch (Exception e) {
            log.warn("Error reading songs by genre {} from read model: {}", genreId, e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<SongReadModel> findByIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            TypedQuery<SongReadModel> query = entityManager.createQuery(
                "SELECT s FROM SongReadModel s WHERE s.id IN :ids", SongReadModel.class);
            query.setParameter("ids", ids);
            query.setHint("org.hibernate.readOnly", true);
            return query.getResultList();
        } catch (Exception e) {
            log.warn("Error reading songs by ids from read model: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public long count() {
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM SongReadModel s", Long.class);
            query.setHint("org.hibernate.readOnly", true);
            return query.getSingleResult();
        } catch (Exception e) {
            log.warn("Error counting songs from read model: {}", e.getMessage());
            return 0L;
        }
    }
}
