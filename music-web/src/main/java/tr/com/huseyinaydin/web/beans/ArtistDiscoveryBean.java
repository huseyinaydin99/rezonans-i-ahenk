package tr.com.huseyinaydin.web.beans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.web.context.annotation.RequestScope;
import jakarta.faces.context.FacesContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;

import java.util.List;
import java.util.Map;

@Component
@RequestScope
public class ArtistDiscoveryBean {
    private static final Logger log = LoggerFactory.getLogger(ArtistDiscoveryBean.class);


    @Autowired
    private IArtistService artistService;

    private List<ResultArtistDto> artists;
    private ArtistDetailDto selectedArtist;
    private String searchQuery;

    @PostConstruct
    public void init() {
        // Tüm sanatçı listesini çeker
        this.artists = artistService.getFeaturedArtists();
    }
    
    // Detay sayfasına girildiğinde URL'den id'yi yakalar
    public void loadArtistDetail() {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        String artistId = params.get("id");
        
        if (artistId != null && !artistId.isEmpty()) {
            this.selectedArtist = artistService.getArtistDetail(artistId);
        }
    }
    
    // Basit JSF içi arama fonksiyonu simülasyonu
    public void searchArtists() {
        log.info("Arama Yapılıyor: " + searchQuery);
        // Gerçekte ElasticSearch veya veritabanı sorgusu atılır
    }

    public List<ResultArtistDto> getArtists() { return artists; }
    public ArtistDetailDto getSelectedArtist() { return selectedArtist; }
    
    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }
}
