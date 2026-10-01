package tr.com.huseyinaydin.domain.playlist;

import tr.com.huseyinaydin.domain.common.Entity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Playlist extends Entity<UUID> {
    private String name;
    private UUID userId;
    private List<PlaylistSong> playlistSongs;

    protected Playlist() {
        super();
    }

    public Playlist(String name, UUID userId) {
        super();
        this.id = UUID.randomUUID();
        this.name = name;
        this.userId = userId;
        this.playlistSongs = new ArrayList<>();
    }

    public void addSong(UUID songId) {
        boolean exists = playlistSongs.stream().anyMatch(ps -> ps.getSongId().equals(songId));
        if (!exists) {
            playlistSongs.add(new PlaylistSong(this.id, songId));
        }
    }

    public void removeSong(UUID songId) {
        playlistSongs.removeIf(ps -> ps.getSongId().equals(songId));
    }

    public void rename(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Playlist name cannot be empty");
        }
        this.name = newName;
    }

    public String getName() { return name; }
    public UUID getUserId() { return userId; }
    public List<PlaylistSong> getPlaylistSongs() { 
        return Collections.unmodifiableList(playlistSongs); 
    }
}
