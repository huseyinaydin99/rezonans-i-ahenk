package tr.com.huseyinaydin.web.beans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IGenreService;
import tr.com.huseyinaydin.application.genre.dtos.GenreBrowseDto;
import tr.com.huseyinaydin.application.genre.dtos.GenreDetailDto;

import java.io.Serializable;
import java.util.List;

@Component
@SessionScope
public class BrowseBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(BrowseBean.class);


    @Autowired
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
        log.info("[JSF AJAX] Müzik türü seçildi. İlgili şarkılar Elastic üzerinden UI'a yüklendi.");
    }

    public List<GenreBrowseDto> getGenres() { return genres; }
    public GenreDetailDto getSelectedGenreDetail() { return selectedGenreDetail; }
}
