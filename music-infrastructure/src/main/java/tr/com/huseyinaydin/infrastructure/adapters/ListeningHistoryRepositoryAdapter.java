package tr.com.huseyinaydin.infrastructure.adapters;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import tr.com.huseyinaydin.application.ports.IListeningHistoryRepository;
import tr.com.huseyinaydin.infrastructure.kafka.ListenEventProducer;
import tr.com.huseyinaydin.infrastructure.nosql.service.CassandraListeningHistoryService;
import tr.com.huseyinaydin.infrastructure.nosql.table.ListeningHistoryTable;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ListeningHistoryRepositoryAdapter implements IListeningHistoryRepository {

    private static final Logger log = LoggerFactory.getLogger(ListeningHistoryRepositoryAdapter.class);

    private final JdbcTemplate jdbcTemplate;
    private final CassandraListeningHistoryService cassandraService;
    private final ListenEventProducer listenEventProducer;

    // L1 In-Memory Cache: Dinlenme sayıları ve geçmişi bellek üzerinde anında tutarlı tutar
    private final Map<Long, AtomicLong> memoryPlayCounts = new ConcurrentHashMap<>();
    private final Map<String, List<Map<String, Object>>> memoryHistories = new ConcurrentHashMap<>();

    public ListeningHistoryRepositoryAdapter(JdbcTemplate jdbcTemplate,
                                            @Autowired(required = false) CassandraListeningHistoryService cassandraService,
                                            @Autowired(required = false) ListenEventProducer listenEventProducer) {
        this.jdbcTemplate = jdbcTemplate;
        this.cassandraService = cassandraService;
        this.listenEventProducer = listenEventProducer;
    }

    @PostConstruct
    public void init() {
        // Tabloları oluştur (H2 / Oracle uyumlu)
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS LISTENING_HISTORIES (" +
                    "ID VARCHAR(36) NOT NULL PRIMARY KEY, " +
                    "USER_EMAIL VARCHAR(255) NOT NULL, " +
                    "SONG_ID NUMERIC NOT NULL, " +
                    "LISTENED_AT TIMESTAMP NOT NULL)");
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("CREATE TABLE LISTENING_HISTORIES (" +
                        "ID VARCHAR(36) NOT NULL PRIMARY KEY, " +
                        "USER_EMAIL VARCHAR(255) NOT NULL, " +
                        "SONG_ID NUMBER NOT NULL, " +
                        "LISTENED_AT TIMESTAMP NOT NULL)");
            } catch (Exception ignored) {}
        }

        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SONG_PLAY_COUNTS (" +
                    "SONG_ID NUMERIC NOT NULL PRIMARY KEY, " +
                    "PLAY_COUNT NUMERIC NOT NULL)");
        } catch (Exception e) {
            try {
                jdbcTemplate.execute("CREATE TABLE SONG_PLAY_COUNTS (" +
                        "SONG_ID NUMBER NOT NULL PRIMARY KEY, " +
                        "PLAY_COUNT NUMBER NOT NULL)");
            } catch (Exception ignored) {}
        }
    }

    @Override
    public long incrementPlayCount(Long songId) {
        if (songId == null) return 0L;

        AtomicLong count = memoryPlayCounts.computeIfAbsent(songId, id -> {
            long initial = 1200000L + (id * 145000L);
            try {
                Long dbVal = jdbcTemplate.queryForObject(
                        "SELECT PLAY_COUNT FROM SONG_PLAY_COUNTS WHERE SONG_ID = ?", Long.class, id);
                if (dbVal != null && dbVal > 0) return new AtomicLong(dbVal);
            } catch (Exception ignored) {}
            return new AtomicLong(initial);
        });

        long newCount = count.incrementAndGet();

        try {
            int updated = jdbcTemplate.update(
                    "UPDATE SONG_PLAY_COUNTS SET PLAY_COUNT = ? WHERE SONG_ID = ?", newCount, songId);
            if (updated == 0) {
                jdbcTemplate.update(
                        "INSERT INTO SONG_PLAY_COUNTS (SONG_ID, PLAY_COUNT) VALUES (?, ?)", songId, newCount);
            }
        } catch (Exception ex) {
            log.warn("[PLAY COUNT] DB senkronizasyon uyarısı (bellek önbelleğinde korundu): {}", ex.getMessage());
        }

        return newCount;
    }

    @Override
    public long getPlayCount(Long songId) {
        if (songId == null) return 0L;
        AtomicLong count = memoryPlayCounts.get(songId);
        if (count != null) return count.get();

        long initial = 1200000L + (songId * 145000L);
        try {
            Long dbVal = jdbcTemplate.queryForObject(
                    "SELECT PLAY_COUNT FROM SONG_PLAY_COUNTS WHERE SONG_ID = ?", Long.class, songId);
            if (dbVal != null && dbVal > 0) {
                memoryPlayCounts.put(songId, new AtomicLong(dbVal));
                return dbVal;
            }
        } catch (Exception ignored) {}

        memoryPlayCounts.put(songId, new AtomicLong(initial));
        return initial;
    }

    @Override
    public void recordListeningHistory(String userEmail, Long songId) {
        if (userEmail == null || userEmail.isBlank() || songId == null) return;
        String email = userEmail.trim().toLowerCase(Locale.ROOT);
        LocalDateTime now = LocalDateTime.now();

        // 1. Bellek listesine ekle
        Map<String, Object> record = new HashMap<>();
        record.put("songId", songId);
        record.put("listenedAt", now);
        memoryHistories.computeIfAbsent(email, k -> Collections.synchronizedList(new ArrayList<>())).add(0, record);

        // 2. İlişkisel Veritabanına Yaz
        try {
            String historyId = UUID.randomUUID().toString();
            jdbcTemplate.update(
                    "INSERT INTO LISTENING_HISTORIES (ID, USER_EMAIL, SONG_ID, LISTENED_AT) VALUES (?, ?, ?, ?)",
                    historyId, email, songId, Timestamp.valueOf(now));
            log.info("✅ [LISTENING HISTORY] Dinleme kaydı ilişkisel veritabanına eklendi: {} -> Şarkı #{}", email, songId);
        } catch (Exception ex) {
            log.warn("[LISTENING HISTORY] DB yazma uyarısı: {}", ex.getMessage());
        }

        // 3. Cassandra NoSQL Kümesine Dağıtık Yaz
        if (cassandraService != null) {
            try {
                UUID userUuid = UUID.nameUUIDFromBytes(email.getBytes(StandardCharsets.UTF_8));
                UUID songUuid = UUID.nameUUIDFromBytes(String.valueOf(songId).getBytes(StandardCharsets.UTF_8));
                cassandraService.saveHistory(new ListeningHistoryTable(userUuid, now, songUuid, 210));
            } catch (Exception ex) {
                log.warn("[LISTENING HISTORY] Cassandra uyarısı: {}", ex.getMessage());
            }
        }

        // 4. Kafka Event Stream'e Asenkron Olay Bırak
        if (listenEventProducer != null) {
            try {
                listenEventProducer.sendListenEvent(email, songId);
            } catch (Exception ex) {
                log.warn("[LISTENING HISTORY] Kafka uyarısı: {}", ex.getMessage());
            }
        }
    }

    @Override
    public List<Map<String, Object>> getRecentHistory(String userEmail, int limit) {
        if (userEmail == null || userEmail.isBlank()) return Collections.emptyList();
        String email = userEmail.trim().toLowerCase(Locale.ROOT);

        List<Map<String, Object>> list = memoryHistories.get(email);
        if (list != null && !list.isEmpty()) {
            return list.subList(0, Math.min(limit, list.size()));
        }

        try {
            return jdbcTemplate.query(
                    "SELECT SONG_ID, LISTENED_AT FROM LISTENING_HISTORIES WHERE LOWER(USER_EMAIL) = ? ORDER BY LISTENED_AT DESC",
                    (rs, rowNum) -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("songId", rs.getLong("SONG_ID"));
                        map.put("listenedAt", rs.getTimestamp("LISTENED_AT").toLocalDateTime());
                        return map;
                    },
                    email
            );
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
}
