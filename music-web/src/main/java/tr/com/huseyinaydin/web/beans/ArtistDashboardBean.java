package tr.com.huseyinaydin.web.beans;

import org.springframework.web.context.annotation.RequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Part;
import tr.com.huseyinaydin.application.ports.IArtistService;
import tr.com.huseyinaydin.application.artist.dtos.ArtistDashboardDto;

import java.io.IOException;

@Component
@RequestScope
public class ArtistDashboardBean {

    @Autowired
    private IArtistService artistService;

    private ArtistDashboardDto stats;
    
    // Şarkı yükleme form alanları
    private String newSongTitle;
    private Part uploadedAudioFile; // JSF <h:inputFile> bileşeni
    
    private String uploadMessage;

    public void loadDashboard() {
        // Gerçekte SecurityContext üzerinden giriş yapan sanatçı ID'si alınır
        this.stats = artistService.getDashboardStats("current-artist-id");
    }

    public void uploadSong() {
        try {
            if (uploadedAudioFile != null && uploadedAudioFile.getSize() > 0) {
                artistService.publishNewSong(
                    "current-artist-id", 
                    newSongTitle, 
                    uploadedAudioFile.getInputStream(), 
                    uploadedAudioFile.getSubmittedFileName(), 
                    uploadedAudioFile.getSize(), 
                    uploadedAudioFile.getContentType()
                );
                
                this.uploadMessage = "Eseriniz başarıyla yayınlandı ve takipçilerinize bildirim gönderildi!";
                this.newSongTitle = "";
                System.out.println("[JSF AJAX] Sanatçı şarkı yükleme işlemi arayüzden başarıyla tetiklendi.");
            } else {
                this.uploadMessage = "Hata: Geçerli bir MP3 dosyası seçmediniz.";
            }
        } catch (Exception e) {
            this.uploadMessage = "Yükleme sırasında hata oluştu: " + e.getMessage();
        }
    }

    // Getters and Setters
    public ArtistDashboardDto getStats() { return stats; }
    public String getNewSongTitle() { return newSongTitle; }
    public void setNewSongTitle(String newSongTitle) { this.newSongTitle = newSongTitle; }
    public Part getUploadedAudioFile() { return uploadedAudioFile; }
    public void setUploadedAudioFile(Part uploadedAudioFile) { this.uploadedAudioFile = uploadedAudioFile; }
    public String getUploadMessage() { return uploadMessage; }
}
