package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IMusicRecommendationService;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GetRecommendedSongsQuery(UUID userId, int limit) implements IQuery<List<SongResponse>>, ICacheableRequest {

    @Override
    public String getCacheKey() {
        return "recommendations:" + userId + ":" + limit;
    }

    @Override
    public String getCacheName() {
        return "recommendationsCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 30;
    }

    @Component
    public static class Handler implements IQueryHandler<GetRecommendedSongsQuery, List<SongResponse>> {
        private final IMusicRecommendationService recommendationService;
        private final ISongReadRepository readRepository;

        public Handler(IMusicRecommendationService recommendationService, ISongReadRepository readRepository) {
            this.recommendationService = recommendationService;
            this.readRepository = readRepository;
        }

        @Override
        public List<SongResponse> handle(GetRecommendedSongsQuery query) {
            List<UUID> recommendedIds = recommendationService.getRecommendationsForUser(query.userId(), query.limit());
            if (recommendedIds == null || recommendedIds.isEmpty()) {
                return Collections.emptyList();
            }

            List<SongReadModel> readModels = readRepository.findByIds(recommendedIds);
            return readModels.stream()
                .map(SongResponse::fromReadModel)
                .collect(Collectors.toList());
        }
    }
}
