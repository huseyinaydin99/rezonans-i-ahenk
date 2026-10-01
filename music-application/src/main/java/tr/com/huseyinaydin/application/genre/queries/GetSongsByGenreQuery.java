package tr.com.huseyinaydin.application.genre.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public record GetSongsByGenreQuery(UUID genreId) implements IQuery<List<SongResponse>> {
    
    @Component
    public static class Handler implements IQueryHandler<GetSongsByGenreQuery, List<SongResponse>> {
        private final ISongRepository songRepository;

        public Handler(ISongRepository songRepository) {
            this.songRepository = songRepository;
        }

        @Override
        public List<SongResponse> handle(GetSongsByGenreQuery query) {
            // Business Rule: Ensure genre exists
            // In a real app, songRepository.findByGenreId(query.genreId()) would be called.
            System.out.println("Fetching all songs for Genre ID: " + query.genreId());
            
            // Simulating returning data for architecture demo
            return Collections.emptyList();
        }
    }
}
