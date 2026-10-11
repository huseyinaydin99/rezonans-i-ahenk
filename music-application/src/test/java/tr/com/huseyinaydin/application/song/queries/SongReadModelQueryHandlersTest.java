package tr.com.huseyinaydin.application.song.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.application.genre.queries.GetSongsByGenreQuery;
import tr.com.huseyinaydin.application.ports.IMusicRecommendationService;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;
import tr.com.huseyinaydin.sharedkernel.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SongReadModelQueryHandlersTest {

    private InMemorySongReadRepository readRepository;
    private StubMusicRecommendationService recommendationService;

    private UUID songId;
    private UUID artistId;
    private UUID genreId;
    private SongReadModel sampleReadModel;

    @BeforeEach
    void setUp() {
        readRepository = new InMemorySongReadRepository();
        recommendationService = new StubMusicRecommendationService();

        songId = UUID.randomUUID();
        artistId = UUID.randomUUID();
        genreId = UUID.randomUUID();

        sampleReadModel = new SongReadModel(
            songId,
            "Ahenk Melodisi",
            "https://cdn.example.com/cover.jpg",
            "https://cdn.example.com/audio.mp3",
            210L,
            1500L,
            "PREMIUM",
            LocalDateTime.now(),
            artistId,
            "Rezonans Grubu",
            genreId,
            "Klasik & Tasavvuf",
            LocalDateTime.now()
        );
        readRepository.save(sampleReadModel);
    }

    @Test
    @DisplayName("GetListSongQuery Handler should return denormalized SongResponses from read model")
    void getListSongQuery_shouldReturnSongResponses() {
        GetListSongQuery.Handler handler = new GetListSongQuery.Handler(readRepository);
        List<SongResponse> result = handler.handle(new GetListSongQuery());

        assertThat(result).hasSize(1);
        SongResponse response = result.get(0);
        assertThat(response.id()).isEqualTo(songId);
        assertThat(response.title()).isEqualTo("Ahenk Melodisi");
        assertThat(response.artistName()).isEqualTo("Rezonans Grubu");
        assertThat(response.genreName()).isEqualTo("Klasik & Tasavvuf");
    }

    @Test
    @DisplayName("GetByIdSongQuery Handler should return SongResponse when found in read repository")
    void getByIdSongQuery_whenFound_shouldReturnSongResponse() {
        GetByIdSongQuery.Handler handler = new GetByIdSongQuery.Handler(readRepository);
        SongResponse response = handler.handle(new GetByIdSongQuery(songId));

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(songId);
        assertThat(response.title()).isEqualTo("Ahenk Melodisi");
        assertThat(response.artistName()).isEqualTo("Rezonans Grubu");
    }

    @Test
    @DisplayName("GetByIdSongQuery Handler should throw NotFoundException when not in read repository")
    void getByIdSongQuery_whenNotFound_shouldThrowNotFoundException() {
        GetByIdSongQuery.Handler handler = new GetByIdSongQuery.Handler(readRepository);
        UUID nonExistingId = UUID.randomUUID();

        assertThatThrownBy(() -> handler.handle(new GetByIdSongQuery(nonExistingId)))
            .isInstanceOf(NotFoundException.class)
            .hasMessageContaining("SONG");
    }

    @Test
    @DisplayName("GetRecommendedSongsQuery Handler should batch load recommendations via read repository")
    void getRecommendedSongsQuery_shouldBatchLoadFromReadRepository() {
        UUID userId = UUID.randomUUID();
        recommendationService.setRecommendations(List.of(songId));

        GetRecommendedSongsQuery.Handler handler = new GetRecommendedSongsQuery.Handler(recommendationService, readRepository);
        List<SongResponse> responses = handler.handle(new GetRecommendedSongsQuery(userId, 5));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).title()).isEqualTo("Ahenk Melodisi");
    }

    @Test
    @DisplayName("GetSongsByGenreQuery Handler should query read repository by genreId")
    void getSongsByGenreQuery_shouldQueryReadRepository() {
        GetSongsByGenreQuery.Handler handler = new GetSongsByGenreQuery.Handler(readRepository);
        List<SongResponse> responses = handler.handle(new GetSongsByGenreQuery(genreId));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).genreName()).isEqualTo("Klasik & Tasavvuf");
    }

    private static class InMemorySongReadRepository implements ISongReadRepository {
        private final Map<UUID, SongReadModel> map = new HashMap<>();

        void save(SongReadModel model) {
            map.put(model.getId(), model);
        }

        @Override
        public Optional<SongReadModel> findById(UUID id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public List<SongReadModel> findAll(int pageIndex, int pageSize) {
            return new ArrayList<>(map.values());
        }

        @Override
        public List<SongReadModel> findByGenreId(UUID genreId) {
            return map.values().stream()
                .filter(s -> Objects.equals(s.getGenreId(), genreId))
                .collect(Collectors.toList());
        }

        @Override
        public List<SongReadModel> findByIds(List<UUID> ids) {
            return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        }

        @Override
        public long count() {
            return map.size();
        }
    }

    private static class StubMusicRecommendationService implements IMusicRecommendationService {
        private List<UUID> recommendations = Collections.emptyList();

        void setRecommendations(List<UUID> recommendations) {
            this.recommendations = recommendations;
        }

        @Override
        public List<UUID> getRecommendationsForUser(UUID userId, int limit) {
            return recommendations;
        }
    }
}
