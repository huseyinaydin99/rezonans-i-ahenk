package tr.com.huseyinaydin.application.search.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;

public record GlobalSearchQuery(String keyword) implements IQuery<GlobalSearchResponse> {
    
    @Component
    public static class Handler implements IQueryHandler<GlobalSearchQuery, GlobalSearchResponse> {
        
        private final ISongRepository songRepository;
        private final IArtistRepository artistRepository;
        private final IPlaylistRepository playlistRepository;

        public Handler(ISongRepository songRepository, IArtistRepository artistRepository, IPlaylistRepository playlistRepository) {
            this.songRepository = songRepository;
            this.artistRepository = artistRepository;
            this.playlistRepository = playlistRepository;
        }

        @Override
        public GlobalSearchResponse handle(GlobalSearchQuery query) {
            String q = query.keyword().toLowerCase();
            System.out.println("Global Search Executing for keyword: " + q);
            
            // In a real scenario, this would use custom repository methods:
            // e.g., songRepository.searchByTitle(q)
            
            // Simulating empty/mocked results for architectural completion
            return new GlobalSearchResponse(
                Collections.emptyList(), // Songs matching keyword
                Collections.emptyList(), // Artists matching keyword
                Collections.emptyList()  // Playlists matching keyword
            );
        }
    }
}
