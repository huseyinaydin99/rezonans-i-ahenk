package tr.com.huseyinaydin.application.playlist.dtos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlaylistDetailDto {
    private String id;
    private String name;
    private String description;
    private List<String> songTitles;
    private List<Long> songIds;
    private List<Map<String, Object>> songs;

    public PlaylistDetailDto() {
        this.songTitles = Collections.synchronizedList(new ArrayList<>());
        this.songIds = Collections.synchronizedList(new ArrayList<>());
        this.songs = Collections.synchronizedList(new ArrayList<>());
    }

    public PlaylistDetailDto(String id, String name, String description, List<String> songTitles) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.songTitles = songTitles != null ? Collections.synchronizedList(new ArrayList<>(songTitles)) : Collections.synchronizedList(new ArrayList<>());
        this.songIds = Collections.synchronizedList(new ArrayList<>());
        this.songs = Collections.synchronizedList(new ArrayList<>());
    }

    public PlaylistDetailDto(String id, String name, String description, List<String> songTitles, List<Long> songIds, List<Map<String, Object>> songs) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.songTitles = songTitles != null ? Collections.synchronizedList(new ArrayList<>(songTitles)) : Collections.synchronizedList(new ArrayList<>());
        this.songIds = songIds != null ? Collections.synchronizedList(new ArrayList<>(songIds)) : Collections.synchronizedList(new ArrayList<>());
        this.songs = songs != null ? Collections.synchronizedList(new ArrayList<>(songs)) : Collections.synchronizedList(new ArrayList<>());
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getSongTitles() { return songTitles; }
    public void setSongTitles(List<String> songTitles) { this.songTitles = songTitles; }
    public List<Long> getSongIds() { return songIds; }
    public void setSongIds(List<Long> songIds) { this.songIds = songIds; }
    public List<Map<String, Object>> getSongs() { return songs; }
    public void setSongs(List<Map<String, Object>> songs) { this.songs = songs; }
    public int getSongCount() {
        if (songs != null && !songs.isEmpty()) return songs.size();
        if (songTitles != null && !songTitles.isEmpty()) return songTitles.size();
        if (songIds != null && !songIds.isEmpty()) return songIds.size();
        return 0;
    }
}
