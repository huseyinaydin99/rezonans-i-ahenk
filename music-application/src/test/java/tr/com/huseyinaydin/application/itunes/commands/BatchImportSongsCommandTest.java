package tr.com.huseyinaydin.application.itunes.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.application.itunes.dtos.BatchImportResultDto;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesTrackDto;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.ports.ISongSearchService;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class BatchImportSongsCommandTest {

    private FakeItunesService fakeItunesService;
    private FakeSongSearchService fakeSongSearchService;
    private BatchImportSongsCommand.Handler handler;

    @BeforeEach
    void setUp() {
        fakeItunesService = new FakeItunesService();
        fakeSongSearchService = new FakeSongSearchService();

        // Varsayılan synthwave sanatçıları için taklit parçalar ekle
        fakeItunesService.addTracksForArtist("The Midnight", List.of(
                new ItunesTrackDto("Sunset", "The Midnight", "http://art/100x100.jpg", "http://preview1.mp3", 320000, "Electronic"),
                new ItunesTrackDto("Vampires", "The Midnight", "http://art/100x100.jpg", "http://preview2.mp3", 310000, "Electronic")
        ));
        fakeItunesService.addTracksForArtist("Kavinsky", List.of(
                new ItunesTrackDto("Nightcall", "Kavinsky", "http://art/100x100.jpg", "http://preview3.mp3", 260000, "Synthwave")
        ));

        handler = new BatchImportSongsCommand.Handler(
                fakeItunesService,
                null, // songRepository optional
                fakeSongSearchService
        );
    }

    @Test
    @DisplayName("Toplu içe aktarma belirtilen sanatçıların parçalarını kataloğa eklemelidir")
    void handle_shouldImportTracksSuccessfully() {
        BatchImportSongsCommand command = new BatchImportSongsCommand(List.of("The Midnight", "Kavinsky"), 5);
        BatchImportResultDto result = handler.handle(command);

        assertThat(result).isNotNull();
        assertThat(result.totalArtistsProcessed()).isEqualTo(2);
        assertThat(result.totalFound()).isEqualTo(3);
        assertThat(result.totalImported()).isEqualTo(3);
        assertThat(result.totalSkipped()).isEqualTo(0);
        assertThat(result.importedTracks()).hasSize(3);

        // Mapped genres and artwork upscale check
        BatchImportResultDto.ImportedTrackSummary sunset = result.importedTracks().get(0);
        assertThat(sunset.title()).isEqualTo("Sunset");
        assertThat(sunset.artist()).isEqualTo("The Midnight");
        assertThat(sunset.genre()).isEqualTo("Synthwave");
        assertThat(sunset.coverUrl()).contains("600x600");

        // Kataloğa eklenen parçalar doğrulanmalı
        assertThat(fakeSongSearchService.registeredSongs).hasSize(3);
    }

    @Test
    @DisplayName("Katalogda zaten mevcut olan parçalar mükerrer kontrolü ile atlanmalıdır")
    void handle_shouldSkipDuplicateTracks() {
        // Önceden kataloğa "Sunset" parçasını ekle
        fakeSongSearchService.existingSongs.add(Map.of(
                "id", 101L,
                "title", "Sunset",
                "artist", "The Midnight"
        ));

        BatchImportSongsCommand command = new BatchImportSongsCommand(List.of("The Midnight"), 5);
        BatchImportResultDto result = handler.handle(command);

        assertThat(result.totalFound()).isEqualTo(2);
        assertThat(result.totalImported()).isEqualTo(1); // Vampires eklendi
        assertThat(result.totalSkipped()).isEqualTo(1);  // Sunset atlandı
        assertThat(result.importedTracks()).hasSize(1);
        assertThat(result.importedTracks().get(0).title()).isEqualTo("Vampires");
    }

    // --- Fakes ---

    private static class FakeItunesService implements IItunesService {
        private final Map<String, List<ItunesTrackDto>> storage = new HashMap<>();

        void addTracksForArtist(String artist, List<ItunesTrackDto> tracks) {
            storage.put(artist.toLowerCase(Locale.ROOT), tracks);
        }

        @Override
        public ItunesResponseDto searchMusic(String term, int limit) {
            List<ItunesTrackDto> list = storage.getOrDefault(term.toLowerCase(Locale.ROOT), Collections.emptyList());
            int end = Math.min(list.size(), limit > 0 ? limit : list.size());
            List<ItunesTrackDto> sub = list.subList(0, end);
            return new ItunesResponseDto(sub.size(), sub);
        }
    }

    private static class FakeSongSearchService implements ISongSearchService {
        final List<Map<String, Object>> existingSongs = new ArrayList<>();
        final List<Map<String, Object>> registeredSongs = new ArrayList<>();

        @Override
        public List<Map<String, Object>> searchSongs(String keyword) {
            List<Map<String, Object>> combined = new ArrayList<>(existingSongs);
            combined.addAll(registeredSongs);
            return combined;
        }

        @Override
        public Map<String, Object> getSongById(Long songId) {
            return null;
        }

        @Override
        public List<Map<String, Object>> getSongsByIds(List<Long> songIds) {
            return Collections.emptyList();
        }

        @Override
        public Map<String, Integer> getGenreSongCounts() {
            return Collections.emptyMap();
        }

        @Override
        public void registerSong(Long id, String title, String artist, String genre, String tier, String coverUrl, String duration) {
            registeredSongs.add(Map.of(
                    "id", id,
                    "title", title,
                    "artist", artist,
                    "genre", genre,
                    "tier", tier
            ));
        }
    }
}
