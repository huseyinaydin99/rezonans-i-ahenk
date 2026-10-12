package tr.com.huseyinaydin.application.song.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.application.ports.IListeningHistoryRepository;
import tr.com.huseyinaydin.application.ports.IPlaybackService;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.application.profile.dtos.UpdateProfileRequest;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.application.song.dtos.PlaySongResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;

class PlaySongCommandTest {

    private FakeSongSearchService songSearchService;
    private FakePlaybackService playbackService;
    private FakeListeningHistoryRepository listeningHistoryRepo;
    private FakeProfileService profileService;
    private PlaySongCommand.Handler handler;

    @BeforeEach
    void setUp() {
        songSearchService = new FakeSongSearchService();
        playbackService = new FakePlaybackService();
        listeningHistoryRepo = new FakeListeningHistoryRepository();
        profileService = new FakeProfileService();

        handler = new PlaySongCommand.Handler(
                playbackService,
                songSearchService,
                listeningHistoryRepo,
                profileService
        );

        // Şarkıları katalog servisine ekle
        songSearchService.addSong(1L, "Basic Melodi", "Sanatçı A", "Basic", "https://cdn/audio1.mp3");
        songSearchService.addSong(2L, "Gold Melodi", "Sanatçı B", "Gold", "https://cdn/audio2.mp3");
        songSearchService.addSong(3L, "Premium Melodi", "Sanatçı C", "Premium", "https://cdn/audio3.mp3");
        songSearchService.addSong(4L, "Elit Melodi", "Sanatçı D", "Elit", "https://cdn/audio4.mp3");
    }

    @Test
    @DisplayName("Basic kullanıcı Basic şarkıyı çalabilmeli ve dinlenme sayısı artmalıdır")
    void basicUser_canPlayBasicSong() {
        PlaySongCommand command = new PlaySongCommand(1L, "user@test.com", "Basic");
        PlaySongResult result = handler.handle(command);

        assertThat(result.authorized()).isTrue();
        assertThat(result.songId()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Basic Melodi");
        assertThat(result.audioUrl()).isNotEmpty();
        assertThat(result.playCount()).isGreaterThan(0L);
        assertThat(listeningHistoryRepo.recordedEvents).hasSize(1);
    }

    @Test
    @DisplayName("Basic kullanıcı Premium şarkıyı çalmaya çalıştığında 403 / yetki reddi almalıdır")
    void basicUser_cannotPlayPremiumSong() {
        PlaySongCommand command = new PlaySongCommand(3L, "user@test.com", "Basic");
        PlaySongResult result = handler.handle(command);

        assertThat(result.authorized()).isFalse();
        assertThat(result.requiredTier()).isEqualTo("Premium");
        assertThat(result.message()).contains("en az Premium");
        assertThat(listeningHistoryRepo.recordedEvents).isEmpty();
    }

    @Test
    @DisplayName("Premium kullanıcı Premium şarkıyı başarıyla çalabilmelidir")
    void premiumUser_canPlayPremiumSong() {
        PlaySongCommand command = new PlaySongCommand(3L, "user@test.com", "Premium");
        PlaySongResult result = handler.handle(command);

        assertThat(result.authorized()).isTrue();
        assertThat(result.title()).isEqualTo("Premium Melodi");
        assertThat(listeningHistoryRepo.recordedEvents).hasSize(1);
    }

    @Test
    @DisplayName("Kullanıcı profilinde daha üst bir paket varsa (örneğin Elit) yetki tanınmalıdır")
    void userWithUpgradedProfile_canPlayEliteSong() {
        profileService.setProfile("upgraded@test.com", new UserProfileDto("Ali Veli", "ali", "upgraded@test.com", "bio", "Elit"));

        // Command parametresinde token 'Basic' gelse bile profildeki 'Elit' üstün gelmeli
        PlaySongCommand command = new PlaySongCommand(4L, "upgraded@test.com", "Basic");
        PlaySongResult result = handler.handle(command);

        assertThat(result.authorized()).isTrue();
        assertThat(result.title()).isEqualTo("Elit Melodi");
    }

    // --- Test Fakes ---

    private static class FakeSongSearchService implements ISongSearchService {
        private final Map<Long, Map<String, Object>> songs = new HashMap<>();

        void addSong(Long id, String title, String artist, String tier, String audioUrl) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("title", title);
            map.put("artist", artist);
            map.put("tier", tier);
            map.put("cover", "https://cdn/cover.jpg");
            songs.put(id, map);
        }

        @Override
        public List<Map<String, Object>> searchSongs(String keyword) { return new ArrayList<>(songs.values()); }
        @Override
        public Map<String, Object> getSongById(Long songId) { return songs.get(songId); }
        @Override
        public List<Map<String, Object>> getSongsByIds(List<Long> songIds) { return Collections.emptyList(); }
        @Override
        public Map<String, Integer> getGenreSongCounts() { return Collections.emptyMap(); }
    }

    private static class FakePlaybackService implements IPlaybackService {
        @Override public void recordListenEvent(String userId, Long songId) {}
        @Override public String getAudioUrl(Long songId) { return "https://cdn.example.com/stream/" + songId + ".mp3"; }
        @Override public boolean hasAccess(Long songId, String userTier) { return true; }
        @Override public Integer getRequiredTierLevel(Long songId) { return 0; }
    }

    private static class FakeListeningHistoryRepository implements IListeningHistoryRepository {
        final List<String> recordedEvents = new ArrayList<>();
        private final AtomicLong counter = new AtomicLong(10);

        @Override
        public long incrementPlayCount(Long songId) {
            return counter.incrementAndGet();
        }

        @Override
        public long getPlayCount(Long songId) {
            return counter.get();
        }

        @Override
        public void recordListeningHistory(String userEmail, Long songId) {
            recordedEvents.add(userEmail + ":" + songId);
        }

        @Override
        public List<Map<String, Object>> getRecentHistory(String userEmail, int limit) {
            return Collections.emptyList();
        }
    }

    private static class FakeProfileService implements IProfileService {
        private final Map<String, UserProfileDto> map = new HashMap<>();

        void setProfile(String email, UserProfileDto dto) {
            map.put(email, dto);
        }

        @Override public void updateProfile(UpdateProfileRequest request) {}
        @Override public UserProfileDto getProfile(String email) { return map.get(email); }
        @Override public void updateProfileInfo(String currentEmail, String newEmail, String fullName, String username, String bio) {}
    }
}
