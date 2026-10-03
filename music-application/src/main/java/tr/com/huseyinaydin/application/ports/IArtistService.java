package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.artist.dtos.ArtistDashboardDto;
import java.io.InputStream;

public interface IArtistService {
    ArtistDashboardDto getDashboardStats(String artistId);
    void publishNewSong(String artistId, String title, InputStream audioStream, String fileName, long fileSize, String contentType);
}
