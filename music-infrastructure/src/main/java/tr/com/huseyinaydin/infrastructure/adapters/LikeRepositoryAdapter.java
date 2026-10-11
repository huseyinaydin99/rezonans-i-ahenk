package tr.com.huseyinaydin.infrastructure.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import tr.com.huseyinaydin.application.ports.ILikeRepository;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class LikeRepositoryAdapter implements ILikeRepository {
    private static final Logger log = LoggerFactory.getLogger(LikeRepositoryAdapter.class);

    private final JdbcTemplate jdbcTemplate;

    // L1 In-Memory Cache: Sayfa yenileme ve transient DB gecikmelerinde anında tutarlılık sağlar
    private final Map<String, Set<Long>> memoryLikes = new ConcurrentHashMap<>();

    public LikeRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        // Tablo yoksa oluştur (Oracle/H2 uyumlu yapı).
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SONG_LIKES (" +
                                 "USER_EMAIL VARCHAR(255) NOT NULL, " +
                                 "SONG_ID NUMBER NOT NULL, " +
                                 "PRIMARY KEY (USER_EMAIL, SONG_ID))");
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("CREATE TABLE SONG_LIKES (" +
                                     "USER_EMAIL VARCHAR(255) NOT NULL, " +
                                     "SONG_ID NUMERIC NOT NULL, " +
                                     "PRIMARY KEY (USER_EMAIL, SONG_ID))");
            } catch (Exception ex) {
                // Tablo zaten mevcutsa yoksay
            }
        }
    }

    private String normalizeEmail(String email) {
        return (email != null) ? email.trim().toLowerCase(Locale.ROOT) : "";
    }

    @Override
    public void addLike(String userEmail, Long songId) {
        String email = normalizeEmail(userEmail);
        if (email.isEmpty() || songId == null) return;

        // Önbelleği yükle/al ve ekle
        getLikedSongIds(email);
        Set<Long> userSet = memoryLikes.get(email);
        if (userSet != null) {
            userSet.add(songId);
        }

        // Oracle / JDBC Veritabanına Yaz
        try {
            if (!isLikedInDb(email, songId)) {
                jdbcTemplate.update("INSERT INTO SONG_LIKES (USER_EMAIL, SONG_ID) VALUES (?, ?)", email, songId);
            }
        } catch (Exception e) {
            log.warn("[LIKE REPOSITORY] DB insert uyarısı (bellek önbelleğinde korundu): {}", e.getMessage());
        }
    }

    @Override
    public void removeLike(String userEmail, Long songId) {
        String email = normalizeEmail(userEmail);
        if (email.isEmpty() || songId == null) return;

        // Önbelleği yükle/al ve çıkar
        getLikedSongIds(email);
        Set<Long> userSet = memoryLikes.get(email);
        if (userSet != null) {
            userSet.remove(songId);
        }

        // Oracle / JDBC Veritabanından Sil
        try {
            jdbcTemplate.update("DELETE FROM SONG_LIKES WHERE LOWER(USER_EMAIL) = ? AND SONG_ID = ?", email, songId);
        } catch (Exception e) {
            log.warn("[LIKE REPOSITORY] DB delete uyarısı: {}", e.getMessage());
        }
    }

    @Override
    public boolean isLiked(String userEmail, Long songId) {
        String email = normalizeEmail(userEmail);
        if (email.isEmpty() || songId == null) return false;

        Set<Long> userSet = memoryLikes.get(email);
        if (userSet != null) {
            return userSet.contains(songId);
        }

        return getLikedSongIds(email).contains(songId);
    }

    private boolean isLikedInDb(String email, Long songId) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM SONG_LIKES WHERE LOWER(USER_EMAIL) = ? AND SONG_ID = ?",
                Integer.class, email, songId);
            return count != null && count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<Long> getLikedSongIds(String userEmail) {
        String email = normalizeEmail(userEmail);
        if (email.isEmpty()) return Collections.emptyList();

        Set<Long> userSet = memoryLikes.computeIfAbsent(email, k -> {
            Set<Long> set = ConcurrentHashMap.newKeySet();
            try {
                List<Long> dbList = jdbcTemplate.query(
                    "SELECT SONG_ID FROM SONG_LIKES WHERE LOWER(USER_EMAIL) = ?",
                    (rs, rowNum) -> rs.getLong("SONG_ID"),
                    email
                );
                if (dbList != null) {
                    set.addAll(dbList);
                }
            } catch (Exception e) {
                log.warn("[LIKE REPOSITORY] DB listeleme uyarısı: {}", e.getMessage());
            }
            return set;
        });

        return new ArrayList<>(userSet);
    }
}
