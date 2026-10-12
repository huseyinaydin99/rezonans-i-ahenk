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
 * Şarkı arama sorgusu (başlık, sanatçı veya tür filtresine göre).
 */
public record SearchSongsQuery(String query) implements IQuery<List<Map<String, Object>>> {
    private static final Logger log = LoggerFactory.getLogger(SearchSongsQuery.class);

    @Component
    public static class Handler implements IQueryHandler<SearchSongsQuery, List<Map<String, Object>>> {
        private final ISongSearchService songSearchService;

        public Handler(@Autowired(required = false) ISongSearchService songSearchService) {
            this.songSearchService = songSearchService;
        }

        @Override
        public List<Map<String, Object>> handle(SearchSongsQuery query) {
            String q = (query.query() != null) ? query.query().trim() : "";
            log.info("Executing SearchSongsQuery via Mediator for term: '{}'", q);
            if (!q.isEmpty() && songSearchService != null) {
                return songSearchService.searchSongs(q);
            }
            return Collections.emptyList();
        }
    }
}
