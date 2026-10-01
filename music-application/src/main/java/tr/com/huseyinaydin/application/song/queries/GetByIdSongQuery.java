package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.rules.SongBusinessRules;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.UUID;

public record GetByIdSongQuery(UUID id) implements IQuery<SongResponse> {

    @Component
    public static class Handler implements IQueryHandler<GetByIdSongQuery, SongResponse> {

        private final ISongRepository songRepository;
        private final SongBusinessRules businessRules;

        public Handler(ISongRepository songRepository, SongBusinessRules businessRules) {
            this.songRepository = songRepository;
            this.businessRules = businessRules;
        }

        @Override
        public SongResponse handle(GetByIdSongQuery query) {
            businessRules.songMustExistWhenRequested(query.id());
            
            Song song = songRepository.findById(query.id()).get();
            
            return new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getCoverImageUrl(),
                    song.getAudioUrl(),
                    song.getDuration().getSeconds(),
                    song.getPlayCount(),
                    song.getTier().name(),
                    song.getReleaseDate(),
                    song.getArtistId(),
                    song.getGenreId()
            );
        }
    }
}
