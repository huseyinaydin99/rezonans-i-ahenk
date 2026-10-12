package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import java.util.List;

public interface IPlaylistService {
    void createPlaylist(CreatePlaylistDto dto);
    boolean addSongToPlaylist(String playlistId, String songId);
    void removeSongFromPlaylist(String playlistId, String songId);
    List<PlaylistDetailDto> getUserPlaylists(String userId);
    PlaylistDetailDto getPlaylistById(String playlistId);
    void migrateUser(String oldUserId, String newUserId);
}
