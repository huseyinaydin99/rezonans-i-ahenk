package tr.com.huseyinaydin.application.artist.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;
import java.util.List;

/**
 * Öne çıkan 12 retro sanatçıyı getiren CQRS sorgusu.
 */
public record GetFeaturedArtistsQuery() implements IQuery<List<ResultArtistDto>>, ICacheableRequest {
    private static final Logger log = LoggerFactory.getLogger(GetFeaturedArtistsQuery.class);

    @Override
    public String getCacheKey() {
        return "artists:featured:all";
    }

    @Override
    public String getCacheName() {
        return "artistsCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 60;
    }

    @Component
    public static class Handler implements IQueryHandler<GetFeaturedArtistsQuery, List<ResultArtistDto>> {
        private final IArtistService artistService;

        public Handler(@Autowired(required = false) IArtistService artistService) {
            this.artistService = artistService;
        }

        @Override
        public List<ResultArtistDto> handle(GetFeaturedArtistsQuery query) {
            log.info("Executing GetFeaturedArtistsQuery through Mediator");
            if (artistService != null) {
                return artistService.getFeaturedArtists();
            }
            return Collections.emptyList();
        }
    }
}
