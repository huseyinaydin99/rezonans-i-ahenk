package tr.com.huseyinaydin.application.song.rules;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;
import tr.com.huseyinaydin.domain.repositories.IGenreRepository;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.sharedkernel.exception.NotFoundException;
import java.util.UUID;

@Component
public class SongBusinessRules {
    
    private final ISongRepository songRepository;
    private final IArtistRepository artistRepository;
    private final IGenreRepository genreRepository;

    public SongBusinessRules(ISongRepository songRepository, IArtistRepository artistRepository, IGenreRepository genreRepository) {
        this.songRepository = songRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
    }

    public void songMustExistWhenRequested(UUID id) {
        if (!songRepository.findById(id).isPresent()) {
            throw new NotFoundException("SONG", id.toString());
        }
    }

    public void artistMustExist(UUID artistId) {
        if (!artistRepository.findById(artistId).isPresent()) {
            throw new NotFoundException("ARTIST", artistId.toString());
        }
    }

    public void genreMustExist(UUID genreId) {
        if (!genreRepository.findById(genreId).isPresent()) {
            throw new NotFoundException("GENRE", genreId.toString());
        }
    }
}
