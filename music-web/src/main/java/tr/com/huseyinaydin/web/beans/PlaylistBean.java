package tr.com.huseyinaydin.web.beans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;

import java.io.Serializable;
import java.util.List;

@Component
@SessionScope
public class PlaylistBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(PlaylistBean.class);


    @Autowired
    private IPlaylistService playlistService;

    private String newPlaylistName;
    private String newPlaylistDescription;
    private List<PlaylistDetailDto> userPlaylists;
    
    // YENİ: Global çalma listesi ekleme akışı için geçici değişkenler
    private String selectedSongId;
    private String selectedSongTitle;
    private String successMessage;

    public void loadPlaylists() {
        this.userPlaylists = playlistService.getUserPlaylists("current-user-id");
    }

    public void createNewPlaylist() {
        if (newPlaylistName != null && !newPlaylistName.trim().isEmpty()) {
            CreatePlaylistDto dto = new CreatePlaylistDto();
            dto.setUserId("current-user-id");
            dto.setName(newPlaylistName);
            dto.setDescription(newPlaylistDescription);
            
            playlistService.createPlaylist(dto);
            
            this.newPlaylistName = "";
            this.newPlaylistDescription = "";
            loadPlaylists();
            
            log.info("[JSF AJAX] Yeni çalma listesi oluşturuldu ve liste arayüzde (UI) yenilendi.");
        }
    }

    // Modal açılırken o anki şarkının bilgilerini Bean'e aktarır
    public void prepareAddSong(String songId, String songTitle) {
        this.selectedSongId = songId;
        this.selectedSongTitle = songTitle;
        this.successMessage = null;
        loadPlaylists(); // Kullanıcının listelerini güncel getir
        log.info("[JSF AJAX] Modal hazırlandı. Seçilen Şarkı: " + songTitle);
    }

    // Asenkron olarak seçilen şarkıyı belirtilen listeye ekler
    public void addSelectedSongToPlaylist(String playlistId) {
        if (selectedSongId != null && playlistId != null) {
            playlistService.addSongToPlaylist(playlistId, selectedSongId);
            this.successMessage = "Şarkı başarıyla listeye eklendi!";
            log.info("[JSF AJAX] '" + selectedSongTitle + "' şarkısı listeye eklendi.");
        }
    }

    // Modal kapatılırken durumu sıfırlar
    public void resetAction() {
        this.selectedSongId = null;
        this.selectedSongTitle = null;
        this.successMessage = null;
    }

    // Getters & Setters
    public String getNewPlaylistName() { return newPlaylistName; }
    public void setNewPlaylistName(String newPlaylistName) { this.newPlaylistName = newPlaylistName; }
    public String getNewPlaylistDescription() { return newPlaylistDescription; }
    public void setNewPlaylistDescription(String newPlaylistDescription) { this.newPlaylistDescription = newPlaylistDescription; }
    public List<PlaylistDetailDto> getUserPlaylists() { return userPlaylists; }
    
    public String getSelectedSongId() { return selectedSongId; }
    public String getSelectedSongTitle() { return selectedSongTitle; }
    public String getSuccessMessage() { return successMessage; }
}
