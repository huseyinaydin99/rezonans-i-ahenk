package tr.com.huseyinaydin.application.artist.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

/**
 * Belirtilen sanatçının detay bilgilerini ve popüler parçalarını getiren CQRS sorgusu.
 */
public record GetArtistDetailQuery(String artistId) implements IQuery<ArtistDetailDto>, ICacheableRequest {
    private static final Logger log = LoggerFactory.getLogger(GetArtistDetailQuery.class);

    @Override
    public String getCacheKey() {
        return "artists:detail:" + (artistId != null ? artistId.trim().toLowerCase() : "unknown");
    }

    @Override
    public String getCacheName() {
        return "artistsCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 30;
    }

    @Component
    public static class Handler implements IQueryHandler<GetArtistDetailQuery, ArtistDetailDto> {
        private final IArtistService artistService;

        public Handler(@Autowired(required = false) IArtistService artistService) {
            this.artistService = artistService;
        }

        @Override
        public ArtistDetailDto handle(GetArtistDetailQuery query) {
            log.info("Executing GetArtistDetailQuery through Mediator for artistId: {}", query.artistId());
            if (artistService != null && query.artistId() != null && !query.artistId().isBlank()) {
                return artistService.getArtistDetail(query.artistId());
            }
            return null;
        }
    }
}
