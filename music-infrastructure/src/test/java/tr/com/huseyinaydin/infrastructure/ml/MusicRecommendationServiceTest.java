package tr.com.huseyinaydin.infrastructure.ml;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;
import tr.com.huseyinaydin.infrastructure.cassandra.IUserRecommendationCassandraRepository;
import tr.com.huseyinaydin.infrastructure.cassandra.UserRecommendationEntity;
import tr.com.huseyinaydin.infrastructure.ml.job.SparkRecommendationJob;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

class MusicRecommendationServiceTest {

    private InMemoryRecommendationCassandraRepository cassandraRepository;
    private InMemorySongReadRepository songReadRepository;
    private SparkRecommendationJob sparkJob;
    private MusicRecommendationServiceImpl recommendationService;

    private UUID userId;
    private UUID sparkSongId1;
    private UUID sparkSongId2;
    private UUID catalogSongId;

    @BeforeEach
    void setUp() {
        cassandraRepository = new InMemoryRecommendationCassandraRepository();
        songReadRepository = new InMemorySongReadRepository();
        sparkJob = new SparkRecommendationJob(cassandraRepository, songReadRepository);
        recommendationService = new MusicRecommendationServiceImpl(cassandraRepository, sparkJob, songReadRepository);

        userId = UUID.randomUUID();
        sparkSongId1 = UUID.randomUUID();
        sparkSongId2 = UUID.randomUUID();
        catalogSongId = UUID.randomUUID();

        SongReadModel catalogSong = new SongReadModel(
            catalogSongId,
            "Popüler Şarkı",
            "https://cdn.example.com/cover.jpg",
            "https://cdn.example.com/audio.mp3",
            180L,
            50000L,
            "PREMIUM",
            LocalDateTime.now(),
            UUID.randomUUID(),
            "Popüler Sanatçı",
            UUID.randomUUID(),
            "Pop",
            LocalDateTime.now()
        );
        songReadRepository.save(catalogSong);
    }

    @Test
    @DisplayName("Should return Spark ML recommendations when precalculated in Cassandra")
    void getRecommendations_whenInCassandra_shouldReturnSparkPredictions() {
        UserRecommendationEntity entity = new UserRecommendationEntity(
            userId,
            List.of(sparkSongId1, sparkSongId2),
            "Spark-ALS-CollaborativeFiltering",
            0.98,
            Instant.now()
        );
        cassandraRepository.save(entity);

        List<UUID> recommendations = recommendationService.getRecommendationsForUser(userId, 5);

        assertThat(recommendations).containsExactly(sparkSongId1, sparkSongId2);
    }

    @Test
    @DisplayName("Should trigger async Spark job and provide smart catalog fallback on cold start")
    void getRecommendations_whenNotInCassandra_shouldFallbackToCatalogAndTriggerSpark() {
        UUID newUserId = UUID.randomUUID();

        List<UUID> recommendations = recommendationService.getRecommendationsForUser(newUserId, 5);

        assertThat(recommendations).contains(catalogSongId);
    }

    @Test
    @DisplayName("SparkRecommendationJob should persist computed ALS recommendations into Cassandra")
    void sparkJob_shouldPersistToCassandra() {
        UUID testUser = UUID.randomUUID();

        sparkJob.generateAndPersistRecommendationsAsync(testUser, 2).join();

        Optional<UserRecommendationEntity> persisted = cassandraRepository.findById(testUser);
        assertThat(persisted).isPresent();
        assertThat(persisted.get().getRecommendedSongIds()).contains(catalogSongId);
        assertThat(persisted.get().getAlgorithmName()).contains("Spark-ALS");
    }

    private static class InMemoryRecommendationCassandraRepository implements IUserRecommendationCassandraRepository {
        private final Map<UUID, UserRecommendationEntity> map = new HashMap<>();

        @Override
        public <S extends UserRecommendationEntity> S insert(S entity) {
            return save(entity);
        }

        @Override
        public <S extends UserRecommendationEntity> List<S> insert(Iterable<S> entities) {
            return saveAll(entities);
        }

        @Override
        public <S extends UserRecommendationEntity> S save(S entity) {
            map.put(entity.getUserId(), entity);
            return entity;
        }

        @Override
        public <S extends UserRecommendationEntity> List<S> saveAll(Iterable<S> entities) {
            List<S> list = new ArrayList<>();
            for (S e : entities) {
                save(e);
                list.add(e);
            }
            return list;
        }

        @Override
        public Optional<UserRecommendationEntity> findById(UUID id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public boolean existsById(UUID id) {
            return map.containsKey(id);
        }

        @Override
        public List<UserRecommendationEntity> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public org.springframework.data.domain.Slice<UserRecommendationEntity> findAll(org.springframework.data.domain.Pageable pageable) {
            return new org.springframework.data.domain.PageImpl<>(new ArrayList<>(map.values()), pageable, map.size());
        }

        @Override
        public List<UserRecommendationEntity> findAllById(Iterable<UUID> ids) {
            List<UserRecommendationEntity> list = new ArrayList<>();
            for (UUID id : ids) {
                if (map.containsKey(id)) list.add(map.get(id));
            }
            return list;
        }

        @Override
        public long count() {
            return map.size();
        }

        @Override
        public void deleteById(UUID id) {
            map.remove(id);
        }

        @Override
        public void delete(UserRecommendationEntity entity) {
            map.remove(entity.getUserId());
        }

        @Override
        public void deleteAllById(Iterable<? extends UUID> ids) {
            for (UUID id : ids) map.remove(id);
        }

        @Override
        public void deleteAll(Iterable<? extends UserRecommendationEntity> entities) {
            for (UserRecommendationEntity e : entities) map.remove(e.getUserId());
        }

        @Override
        public void deleteAll() {
            map.clear();
        }
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
            return Collections.emptyList();
        }

        @Override
        public List<SongReadModel> findByIds(List<UUID> ids) {
            List<SongReadModel> list = new ArrayList<>();
            for (UUID id : ids) {
                if (map.containsKey(id)) list.add(map.get(id));
            }
            return list;
        }

        @Override
        public long count() {
            return map.size();
        }
    }
}
