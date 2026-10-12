package tr.com.huseyinaydin.application.song.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * ID listesine göre şarkı meta verilerini getiren CQRS sorgusu (örn. beğenilen şarkılar listesi için).
 */
public record GetSongsByIdsQuery(List<Long> songIds) implements IQuery<List<Map<String, Object>>> {
    private static final Logger log = LoggerFactory.getLogger(GetSongsByIdsQuery.class);

    @Component
    public static class Handler implements IQueryHandler<GetSongsByIdsQuery, List<Map<String, Object>>> {
        private final ISongSearchService songSearchService;

        public Handler(@Autowired(required = false) ISongSearchService songSearchService) {
            this.songSearchService = songSearchService;
        }

        @Override
        public List<Map<String, Object>> handle(GetSongsByIdsQuery query) {
            log.info("Executing GetSongsByIdsQuery for {} song IDs", query.songIds() != null ? query.songIds().size() : 0);
            if (songSearchService != null && query.songIds() != null && !query.songIds().isEmpty()) {
                return songSearchService.getSongsByIds(query.songIds());
            }
            return Collections.emptyList();
        }
    }
}
