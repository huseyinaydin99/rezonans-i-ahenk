package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IMusicRecommendationService;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GetRecommendedSongsQuery(UUID userId, int limit) implements IQuery<List<SongResponse>> {
    @Component
    public static class Handler implements IQueryHandler<GetRecommendedSongsQuery, List<SongResponse>> {
        private final IMusicRecommendationService recommendationService;
        private final ISongRepository songRepository;

        public Handler(IMusicRecommendationService recommendationService, ISongRepository songRepository) {
            this.recommendationService = recommendationService;
            this.songRepository = songRepository;
        }

        @Override
        public List<SongResponse> handle(GetRecommendedSongsQuery query) {
            List<UUID> recommendedIds = recommendationService.getRecommendationsForUser(query.userId(), query.limit());
            
            return recommendedIds.stream()
                .map(id -> songRepository.findById(id).orElse(null))
                .filter(java.util.Objects::nonNull)
                .map(song -> new SongResponse(
                    song.getId(), song.getTitle(), song.getCoverImageUrl(), song.getAudioUrl(),
                    song.getDuration().getSeconds(), song.getPlayCount(), song.getTier().name(),
                    song.getReleaseDate(), song.getArtistId(), song.getGenreId()
                ))
                .collect(Collectors.toList());
        }
    }
}
