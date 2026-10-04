package tr.com.huseyinaydin.web.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;

import java.util.List;
import java.util.Map;

@Named
@RequestScoped
public class ArtistDiscoveryBean {

    @Inject
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
        System.out.println("Arama Yapılıyor: " + searchQuery);
        // Gerçekte ElasticSearch veya veritabanı sorgusu atılır
    }

    public List<ResultArtistDto> getArtists() { return artists; }
    public ArtistDetailDto getSelectedArtist() { return selectedArtist; }
    
    public String getSearchQuery() { return searchQuery; }
    public void setSearchQuery(String searchQuery) { this.searchQuery = searchQuery; }
}
