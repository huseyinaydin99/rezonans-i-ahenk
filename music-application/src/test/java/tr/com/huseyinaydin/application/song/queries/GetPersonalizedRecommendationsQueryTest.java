package tr.com.huseyinaydin.application.song.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto.SongSnippet;
import tr.com.huseyinaydin.application.ports.IListeningHistoryRepository;
import tr.com.huseyinaydin.application.ports.ISongSearchService;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class GetPersonalizedRecommendationsQueryTest {

    private FakeListeningHistoryRepository listeningHistoryRepo;
    private FakeSongSearchService songSearchService;
    private GetPersonalizedRecommendationsQuery.Handler handler;

    @BeforeEach
    void setUp() {
        listeningHistoryRepo = new FakeListeningHistoryRepository();
        songSearchService = new FakeSongSearchService();
        handler = new GetPersonalizedRecommendationsQuery.Handler(listeningHistoryRepo, songSearchService);

        // Test kataloğu oluştur
        songSearchService.addSong(1L, "Sunset", "The Midnight", "Synthwave", "Gold", 3000000L);
        songSearchService.addSong(2L, "Days of Thunder", "The Midnight", "Synthwave", "Basic", 2500000L);
        songSearchService.addSong(3L, "Nightcall", "Kavinsky", "Synthwave", "Premium", 2800000L);
        songSearchService.addSong(4L, "Houdini", "Dua Lipa", "Pop", "Premium", 3500000L);
        songSearchService.addSong(5L, "Levitating", "Dua Lipa", "Pop", "Gold", 4000000L);
        songSearchService.addSong(6L, "Enjoy the Silence", "Depeche Mode", "Elektronik", "Elit", 5000000L);
    }

    @Test
    @DisplayName("Dinleme geçmişi olmayan kullanıcı için Cold Start popülerlik fallback'i çalışmalıdır")
    void shouldReturnColdStartRecommendations_whenNoHistoryExists() {
        GetPersonalizedRecommendationsQuery query = new GetPersonalizedRecommendationsQuery("newuser@example.com", 3);
        List<SongSnippet> result = handler.handle(query);

        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(3);
        // En popüler şarkı en başta olmalıdır (Enjoy the Silence - 5M dinlenme)
        assertThat(result.get(0).getTitle()).isEqualTo("Enjoy the Silence");
        assertThat(result.get(0).getMatchReason()).contains("hit parçası");
    }

    @Test
    @DisplayName("Kullanıcının geçmişte dinlediği sanatçı ve türe göre henüz dinlemediği parçalar önceliklendirilmelidir")
    void shouldPrioritizeMatchingGenreAndArtist_andBoostUnlistenedSongs() {
        String email = "synthwavefan@example.com";
        // Kullanıcı 1 numaralı şarkıyı (Sunset - The Midnight) dinlemiş olsun
        listeningHistoryRepo.recordListeningHistory(email, 1L);

        GetPersonalizedRecommendationsQuery query = new GetPersonalizedRecommendationsQuery(email, 4);
        List<SongSnippet> result = handler.handle(query);

        assertThat(result).isNotEmpty();
        // 2 numaralı şarkı (Days of Thunder - The Midnight, Synthwave) henüz dinlenmedi ve hem aynı sanatçı hem aynı tür
        // Bu yüzden en üst sırada önerilmelidir!
        SongSnippet topRec = result.get(0);
        assertThat(topRec.getSongId()).isEqualTo(2L);
        assertThat(topRec.getArtist()).isEqualTo("The Midnight");
        assertThat(topRec.getGenre()).isEqualTo("Synthwave");
        assertThat(topRec.getMatchReason()).contains("The Midnight");
    }

    @Test
    @DisplayName("Servisler veya bağımlılıklar null olduğunda dayanıklı statik fallback devreye girmelidir")
    void shouldFallbackGracefully_whenServicesAreNull() {
        GetPersonalizedRecommendationsQuery.Handler isolatedHandler =
                new GetPersonalizedRecommendationsQuery.Handler(null, null);

        List<SongSnippet> result = isolatedHandler.handle(new GetPersonalizedRecommendationsQuery("null@test.com", 5));
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isLessThanOrEqualTo(7);
    }

    // --- Fake Test Sınıfları ---

    private static class FakeListeningHistoryRepository implements IListeningHistoryRepository {
        private final Map<String, List<Map<String, Object>>> histories = new HashMap<>();
        private final Map<Long, Long> playCounts = new HashMap<>();

        @Override
        public long incrementPlayCount(Long songId) {
            long newCount = playCounts.getOrDefault(songId, 0L) + 1;
            playCounts.put(songId, newCount);
            return newCount;
        }

        @Override
        public long getPlayCount(Long songId) {
            return playCounts.getOrDefault(songId, 1000000L);
        }

        @Override
        public void recordListeningHistory(String userEmail, Long songId) {
            Map<String, Object> record = new HashMap<>();
            record.put("songId", songId);
            record.put("listenedAt", LocalDateTime.now());
            histories.computeIfAbsent(userEmail.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(0, record);
        }

        @Override
        public List<Map<String, Object>> getRecentHistory(String userEmail, int limit) {
            List<Map<String, Object>> list = histories.get(userEmail.toLowerCase(Locale.ROOT));
            if (list == null) return Collections.emptyList();
            return list.subList(0, Math.min(limit, list.size()));
        }
    }

    private static class FakeSongSearchService implements ISongSearchService {
        private final Map<Long, Map<String, Object>> catalog = new LinkedHashMap<>();

        void addSong(Long id, String title, String artist, String genre, String tier, Long playCount) {
            Map<String, Object> song = new HashMap<>();
            song.put("id", id);
            song.put("title", title);
            song.put("artist", artist);
            song.put("genre", genre);
            song.put("tier", tier);
            song.put("cover", "https://example.com/cover.jpg");
            song.put("playCount", playCount);
            catalog.put(id, song);
        }

        @Override
        public List<Map<String, Object>> searchSongs(String keyword) {
            return new ArrayList<>(catalog.values());
        }

        @Override
        public Map<String, Object> getSongById(Long songId) {
            return catalog.get(songId);
        }

        @Override
        public List<Map<String, Object>> getSongsByIds(List<Long> songIds) {
            List<Map<String, Object>> res = new ArrayList<>();
            for (Long id : songIds) {
                if (catalog.containsKey(id)) res.add(catalog.get(id));
            }
            return res;
        }

        @Override
        public Map<String, Integer> getGenreSongCounts() {
            return Collections.emptyMap();
        }
    }
}
