package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.artist.dtos.ArtistDashboardDto;
import tr.com.huseyinaydin.application.artist.dtos.ResultArtistDto;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDetailDto;
import java.io.InputStream;
import java.util.List;

public interface IArtistService {
    ArtistDashboardDto getDashboardStats(String artistId);
    void publishNewSong(String artistId, String title, InputStream audioStream, String fileName, long fileSize, String contentType);
    
    // YENİ EKLENEN KEŞİF METOTLARI
    List<ResultArtistDto> getFeaturedArtists();
    ArtistDetailDto getArtistDetail(String artistId);
}
