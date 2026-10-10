package tr.com.huseyinaydin.infrastructure.adapters;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import tr.com.huseyinaydin.application.ports.ILikeRepository;

import jakarta.annotation.PostConstruct;
import java.util.List;

@Repository
public class LikeRepositoryAdapter implements ILikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public LikeRepositoryAdapter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        // Tablo yoksa oluştur (Oracle/H2 uyumlu basit bir yapı). 
        // Gerçek projede Flyway/Liquibase kullanılır.
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SONG_LIKES (" +
                                 "USER_EMAIL VARCHAR(255) NOT NULL, " +
                                 "SONG_ID NUMBER NOT NULL, " +
                                 "PRIMARY KEY (USER_EMAIL, SONG_ID))");
        } catch (Exception e) {
            // Tablo zaten varsa veya Oracle sözdizimi NUMBER yerine INTEGER gerektiriyorsa
            try {
                jdbcTemplate.execute("CREATE TABLE SONG_LIKES (" +
                                     "USER_EMAIL VARCHAR(255) NOT NULL, " +
                                     "SONG_ID NUMERIC NOT NULL, " +
                                     "PRIMARY KEY (USER_EMAIL, SONG_ID))");
            } catch (Exception ex) {
                // Ignore if it already exists
            }
        }
    }

    @Override
    public void addLike(String userEmail, Long songId) {
        if (!isLiked(userEmail, songId)) {
            jdbcTemplate.update("INSERT INTO SONG_LIKES (USER_EMAIL, SONG_ID) VALUES (?, ?)", userEmail, songId);
        }
    }

    @Override
    public void removeLike(String userEmail, Long songId) {
        jdbcTemplate.update("DELETE FROM SONG_LIKES WHERE USER_EMAIL = ? AND SONG_ID = ?", userEmail, songId);
    }

    @Override
    public boolean isLiked(String userEmail, Long songId) {
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM SONG_LIKES WHERE USER_EMAIL = ? AND SONG_ID = ?",
            Integer.class, userEmail, songId);
        return count != null && count > 0;
    }

    @Override
    public List<Long> getLikedSongIds(String userEmail) {
        return jdbcTemplate.query(
            "SELECT SONG_ID FROM SONG_LIKES WHERE USER_EMAIL = ?",
            (rs, rowNum) -> rs.getLong("SONG_ID"),
            userEmail
        );
    }
}
