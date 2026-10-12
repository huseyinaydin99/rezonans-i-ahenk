package tr.com.huseyinaydin.persistence.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import tr.com.huseyinaydin.domain.playlist.Playlist;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class PlaylistJpaRepository extends JpaRepositoryBase<Playlist, UUID> implements IPlaylistRepository {
    private static final Logger log = LoggerFactory.getLogger(PlaylistJpaRepository.class);

    private final Map<UUID, Playlist> memoryPlaylists = new ConcurrentHashMap<>();
    private final Map<UUID, List<Playlist>> userPlaylistsMap = new ConcurrentHashMap<>();

    public PlaylistJpaRepository(EntityManager entityManager) {
        super(entityManager, Playlist.class);
    }

    @Override
    public Playlist save(Playlist entity) {
        if (entity != null) {
            memoryPlaylists.put(entity.getId(), entity);
            if (entity.getUserId() != null) {
                userPlaylistsMap.computeIfAbsent(entity.getUserId(), k -> Collections.synchronizedList(new ArrayList<>())).add(entity);
            }
        }
        try {
            return super.save(entity);
        } catch (Exception e) {
            log.warn("[PLAYLIST REPOSITORY] DB persist uyarısı (bellek önbelleğinde korundu): {}", e.getMessage());
            return entity;
        }
    }

    @Override
    public Optional<Playlist> findById(UUID id) {
        Playlist cached = memoryPlaylists.get(id);
        if (cached != null) {
            return Optional.of(cached);
        }
        try {
            return super.findById(id);
        } catch (Exception e) {
            log.warn("[PLAYLIST REPOSITORY] DB findById hatası: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public List<Playlist> findByUserId(UUID userId) {
        List<Playlist> cached = userPlaylistsMap.get(userId);
        if (cached != null && !cached.isEmpty()) {
            return new ArrayList<>(cached);
        }
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Playlist> query = cb.createQuery(Playlist.class);
            Root<Playlist> root = query.from(Playlist.class);
            query.where(cb.and(
                cb.equal(root.get("ownerId"), userId),
                root.get("deletedDate").isNull()
            ));
            List<Playlist> dbList = entityManager.createQuery(query).getResultList();
            if (dbList != null && !dbList.isEmpty()) {
                userPlaylistsMap.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>())).addAll(dbList);
                for (Playlist p : dbList) {
                    memoryPlaylists.put(p.getId(), p);
                }
                return dbList;
            }
        } catch (Exception e) {
            log.warn("[PLAYLIST REPOSITORY] DB findByUserId uyarısı: {}", e.getMessage());
        }
        return cached != null ? new ArrayList<>(cached) : new ArrayList<>();
    }
}
