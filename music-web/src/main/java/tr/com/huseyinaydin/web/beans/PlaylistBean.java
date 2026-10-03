package tr.com.huseyinaydin.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;

import java.io.Serializable;
import java.util.List;

@Named
@SessionScoped
public class PlaylistBean implements Serializable {

    @Inject
    private IPlaylistService playlistService;

    private String newPlaylistName;
    private String newPlaylistDescription;
    private List<PlaylistDetailDto> userPlaylists;

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
            
            // Formu temizle ve listeyi yenile
            this.newPlaylistName = "";
            this.newPlaylistDescription = "";
            loadPlaylists();
            
            System.out.println("[JSF AJAX] Yeni çalma listesi oluşturuldu ve liste arayüzde (UI) yenilendi.");
        }
    }

    public void addMockSongToPlaylist(String playlistId) {
        playlistService.addSongToPlaylist(playlistId, "mock-song-id-123");
        loadPlaylists();
        System.out.println("[JSF AJAX] Şarkı başarıyla listeye eklendi.");
    }

    // Getters & Setters
    public String getNewPlaylistName() { return newPlaylistName; }
    public void setNewPlaylistName(String newPlaylistName) { this.newPlaylistName = newPlaylistName; }
    public String getNewPlaylistDescription() { return newPlaylistDescription; }
    public void setNewPlaylistDescription(String newPlaylistDescription) { this.newPlaylistDescription = newPlaylistDescription; }
    public List<PlaylistDetailDto> getUserPlaylists() { return userPlaylists; }
}
