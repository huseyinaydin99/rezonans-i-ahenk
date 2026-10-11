package tr.com.huseyinaydin.infrastructure.ml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IMusicRecommendationService;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;
import tr.com.huseyinaydin.infrastructure.cassandra.IUserRecommendationCassandraRepository;
import tr.com.huseyinaydin.infrastructure.cassandra.UserRecommendationEntity;
import tr.com.huseyinaydin.infrastructure.ml.job.SparkRecommendationJob;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Enterprise AI Destekli Müzik Öneri Servisi.
 * Apache Cassandra NoSQL tablosunda depolanan Spark büyük veri analiz sonuçlarını okur.
 * Eğer kullanıcı için önceden hesaplanmış model sonucu yoksa (cold-start),
 * arka planda asenkron Spark hesaplamasını tetikler ve anlık olarak akıllı katalog
 * analitiği ile kullanıcıya gerçek şarkı önerileri sunar.
 */
@Service
public class MusicRecommendationServiceImpl implements IMusicRecommendationService {
    private static final Logger log = LoggerFactory.getLogger(MusicRecommendationServiceImpl.class);

    private final IUserRecommendationCassandraRepository recommendationCassandraRepository;
    private final SparkRecommendationJob sparkRecommendationJob;
    private final ISongReadRepository songReadRepository;
    private final tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics;

    public MusicRecommendationServiceImpl(
            @Autowired(required = false) IUserRecommendationCassandraRepository recommendationCassandraRepository,
            SparkRecommendationJob sparkRecommendationJob,
            ISongReadRepository songReadRepository,
            @Autowired(required = false) tr.com.huseyinaydin.infrastructure.monitoring.metrics.MusicPlatformMetrics metrics) {
        this.recommendationCassandraRepository = recommendationCassandraRepository;
        this.sparkRecommendationJob = sparkRecommendationJob;
        this.songReadRepository = songReadRepository;
        this.metrics = metrics;
    }

    public MusicRecommendationServiceImpl(
            IUserRecommendationCassandraRepository recommendationCassandraRepository,
            SparkRecommendationJob sparkRecommendationJob,
            ISongReadRepository songReadRepository) {
        this(recommendationCassandraRepository, sparkRecommendationJob, songReadRepository, null);
    }

    @Override
    public List<UUID> getRecommendationsForUser(UUID userId, int limit) {
        int safeLimit = limit > 0 ? limit : 10;
        log.info("🎯 [AI RECOMMENDATION] Kullanıcı ({}) için 'Sana Özel' öneriler Cassandra & Spark üzerinden çekiliyor...", userId);

        // 1. Aşama: Cassandra'da Spark tarafından önceden hesaplanmış ML sonuçlarını ara
        if (recommendationCassandraRepository != null) {
            try {
                Optional<UserRecommendationEntity> cassandraResult = recommendationCassandraRepository.findById(userId);
                if (cassandraResult.isPresent()) {
                    UserRecommendationEntity entity = cassandraResult.get();
                    List<UUID> songIds = entity.getRecommendedSongIds();
                    if (songIds != null && !songIds.isEmpty()) {
                        log.info("✨ [CASSANDRA HIT] Spark ALS Makine Öğrenmesi sonuçları Cassandra'dan başarıyla okundu! Model: '{}', Öneri Sayısı: {}",
                                entity.getAlgorithmName(), songIds.size());
                        if (metrics != null) {
                            metrics.incrementRecommendations("Cassandra-Spark-ALS");
                        }
                        return songIds.stream().limit(safeLimit).collect(Collectors.toList());
                    }
                }
            } catch (Exception e) {
                log.warn("⚠️ [CASSANDRA BAĞLANTI UYARISI] Cassandra'dan öneri okunurken hata: {}. Akıllı yedekleme mekanizmasına geçiliyor.", e.getMessage());
            }
        }

        // 2. Aşama: Cassandra'da kayıt yoksa (Cold Start / Yeni Kullanıcı), arka planda asenkron Spark analizini tetikle
        log.info("🤖 [COLD START / ASYNC SPARK] Kullanıcı ({}) için Cassandra'da önbellek bulunamadı. Arka planda Spark AI modeli tetikleniyor...", userId);
        try {
            sparkRecommendationJob.generateAndPersistRecommendationsAsync(userId, safeLimit);
        } catch (Exception e) {
            log.warn("Asenkron Spark işi tetiklenirken hata: {}", e.getMessage());
        }

        // 3. Aşama: Anlık yanıt için CQRS Read kataloğundaki en popüler şarkıları dön (Zero Empty Result)
        List<SongReadModel> catalog = songReadRepository.findAll(0, safeLimit);
        if (!catalog.isEmpty()) {
            log.info("🎵 [FALLBACK ENGINE] Gerçek katalog üzerinden {} adet popüler şarkı önerisi kullanıcıya sunuldu.", catalog.size());
            if (metrics != null) {
                metrics.incrementRecommendations("Catalog-Fallback");
            }
            return catalog.stream().map(SongReadModel::getId).collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
