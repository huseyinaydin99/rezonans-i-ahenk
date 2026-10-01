package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;
import tr.com.huseyinaydin.sharedkernel.pagination.PaginationRequest;

import java.util.List;
import java.util.stream.Collectors;

public record GetListSongQuery() implements IQuery<List<SongResponse>> {
    @Component
    public static class Handler implements IQueryHandler<GetListSongQuery, List<SongResponse>> {
        private final ISongRepository repository;
        public Handler(ISongRepository repository) { this.repository = repository; }
        
        @Override
        public List<SongResponse> handle(GetListSongQuery query) {
            // Simplified for demonstration - fetching first page
            List<Song> songs = repository.findAll(null, new PaginationRequest(0, 50)).getItems();
            return songs.stream().map(song -> new SongResponse(
                    song.getId(), song.getTitle(), song.getCoverImageUrl(), song.getAudioUrl(),
                    song.getDuration().getSeconds(), song.getPlayCount(), song.getTier().name(),
                    song.getReleaseDate(), song.getArtistId(), song.getGenreId()
            )).collect(Collectors.toList());
        }
    }
}
