package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.genre.dtos.GenreBrowseDto;
import tr.com.huseyinaydin.application.genre.dtos.GenreDetailDto;
import java.util.List;

public interface IGenreService {
    List<GenreBrowseDto> getAllGenres();
    GenreDetailDto getTopSongsByGenreId(String genreId);
}
