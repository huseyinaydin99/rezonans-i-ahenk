package tr.com.huseyinaydin.application.search.queries;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;

public record GlobalSearchQuery(String keyword) implements IQuery<GlobalSearchResponse> {
    private static final Logger log = LoggerFactory.getLogger(GlobalSearchQuery.class);

    
    @Component
    public static class Handler implements IQueryHandler<GlobalSearchQuery, GlobalSearchResponse> {
        
        private final ISongRepository songRepository;
        private final IArtistRepository artistRepository;
        private final IPlaylistRepository playlistRepository;
        private final tr.com.huseyinaydin.application.ports.ISongSearchService songSearchService;

        public Handler(ISongRepository songRepository,
                       IArtistRepository artistRepository,
                       IPlaylistRepository playlistRepository,
                       @org.springframework.beans.factory.annotation.Autowired(required = false) tr.com.huseyinaydin.application.ports.ISongSearchService songSearchService) {
            this.songRepository = songRepository;
            this.artistRepository = artistRepository;
            this.playlistRepository = playlistRepository;
            this.songSearchService = songSearchService;
        }

        @Override
        public GlobalSearchResponse handle(GlobalSearchQuery query) {
            String q = query.keyword() != null ? query.keyword().trim() : "";
            log.info("Global Search Executing via Elasticsearch for keyword: {}", q);

            java.util.List<tr.com.huseyinaydin.application.song.dtos.SongResponse> songs = new java.util.ArrayList<>();
            if (!q.isEmpty() && songSearchService != null) {
                java.util.List<java.util.Map<String, Object>> searchResults = songSearchService.searchSongs(q);
                for (java.util.Map<String, Object> item : searchResults) {
                    java.util.UUID songId;
                    try {
                        songId = java.util.UUID.fromString(String.valueOf(item.get("id")));
                    } catch (Exception e) {
                        songId = java.util.UUID.randomUUID();
                    }
                    songs.add(new tr.com.huseyinaydin.application.song.dtos.SongResponse(
                        songId,
                        (String) item.getOrDefault("title", ""),
                        (String) item.getOrDefault("cover", ""),
                        "",
                        0L,
                        0L,
                        (String) item.getOrDefault("tier", "Basic"),
                        java.time.LocalDateTime.now(),
                        java.util.UUID.randomUUID(),
                        java.util.UUID.randomUUID()
                    ));
                }
            }

            return new GlobalSearchResponse(
                songs,
                Collections.emptyList(),
                Collections.emptyList()
            );
        }
    }
}
