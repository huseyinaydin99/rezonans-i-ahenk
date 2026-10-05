package tr.com.huseyinaydin.web.jsf;

import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.itunes.queries.SearchItunesQuery;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesTrackDto;
import java.util.List;

@Component
@RequestScope
public class ItunesBean {

    @Autowired
    private Mediator mediator;

    private String searchTerm;
    private List<ItunesTrackDto> searchResults;
    private boolean hasSearched = false;

    public void search() {
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            ItunesResponseDto response = mediator.query(new SearchItunesQuery(searchTerm, 12));
            if (response != null && response.results() != null) {
                this.searchResults = response.results();
            }
            this.hasSearched = true;
        }
    }

    public void importTrack(ItunesTrackDto track) {
        // Burada sisteme (veritabanına) ImportSongsCommand veya CreateSongCommand tetiklenir.
        System.out.println(track.trackName() + " isimli şarkı kütüphaneye eklendi!");
    }

    public String getSearchTerm() { return searchTerm; }
    public void setSearchTerm(String searchTerm) { this.searchTerm = searchTerm; }
    public List<ItunesTrackDto> getSearchResults() { return searchResults; }
    public boolean isHasSearched() { return hasSearched; }
}
