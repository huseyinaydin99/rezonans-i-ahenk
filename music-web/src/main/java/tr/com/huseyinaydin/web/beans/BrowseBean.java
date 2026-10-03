package tr.com.huseyinaydin.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.IGenreService;
import tr.com.huseyinaydin.application.genre.dtos.GenreBrowseDto;
import tr.com.huseyinaydin.application.genre.dtos.GenreDetailDto;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class BrowseBean implements Serializable {

    @Inject
    private IGenreService genreService;

    private List<GenreBrowseDto> genres;
    private GenreDetailDto selectedGenreDetail;

    public void loadGenres() {
        if (genres == null) {
            this.genres = genreService.getAllGenres();
        }
    }

    public void selectGenre(String genreId) {
        this.selectedGenreDetail = genreService.getTopSongsByGenreId(genreId);
        System.out.println("[JSF AJAX] Müzik türü seçildi. İlgili şarkılar Elastic üzerinden UI'a yüklendi.");
    }

    public List<GenreBrowseDto> getGenres() { return genres; }
    public GenreDetailDto getSelectedGenreDetail() { return selectedGenreDetail; }
}
