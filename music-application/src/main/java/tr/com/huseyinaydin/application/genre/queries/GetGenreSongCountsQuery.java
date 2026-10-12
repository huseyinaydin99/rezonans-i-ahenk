package tr.com.huseyinaydin.application.genre.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;
import java.util.Map;

/**
 * Platformdaki türlere ait toplam şarkı sayılarını dönen CQRS sorgusu.
 */
public record GetGenreSongCountsQuery() implements IQuery<Map<String, Integer>>, ICacheableRequest {
    private static final Logger log = LoggerFactory.getLogger(GetGenreSongCountsQuery.class);

    @Override
    public String getCacheKey() {
        return "genre:counts:all";
    }

    @Override
    public String getCacheName() {
        return "genresCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 30;
    }

    @Component
    public static class Handler implements IQueryHandler<GetGenreSongCountsQuery, Map<String, Integer>> {
        private final ISongSearchService songSearchService;

        public Handler(@Autowired(required = false) ISongSearchService songSearchService) {
            this.songSearchService = songSearchService;
        }

        @Override
        public Map<String, Integer> handle(GetGenreSongCountsQuery query) {
            log.info("Executing GetGenreSongCountsQuery through Mediator");
            if (songSearchService != null) {
                return songSearchService.getGenreSongCounts();
            }
            return Collections.emptyMap();
        }
    }
}
