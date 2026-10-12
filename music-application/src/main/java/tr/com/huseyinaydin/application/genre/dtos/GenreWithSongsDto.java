package tr.com.huseyinaydin.application.genre.dtos;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class GenreWithSongsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String tag;
    private String description;
    private String themeClass;
    private String accentColor;
    private int songCount;
    private List<GenreSongDto> songs;

    public GenreWithSongsDto() {
        this.songs = Collections.emptyList();
    }

    public GenreWithSongsDto(String id, String name, String tag, String description,
                             String themeClass, String accentColor, int songCount,
                             List<GenreSongDto> songs) {
        this.id = id;
        this.name = name;
        this.tag = tag;
        this.description = description;
        this.themeClass = themeClass;
        this.accentColor = accentColor;
        this.songCount = songCount;
        this.songs = songs != null ? songs : Collections.emptyList();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getThemeClass() { return themeClass; }
    public void setThemeClass(String themeClass) { this.themeClass = themeClass; }

    public String getAccentColor() { return accentColor; }
    public void setAccentColor(String accentColor) { this.accentColor = accentColor; }

    public int getSongCount() { return songCount; }
    public void setSongCount(int songCount) { this.songCount = songCount; }

    public List<GenreSongDto> getSongs() { return songs; }
    public void setSongs(List<GenreSongDto> songs) {
        this.songs = songs != null ? songs : Collections.emptyList();
        this.songCount = this.songs.size();
    }
}
