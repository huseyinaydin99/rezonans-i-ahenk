package tr.com.huseyinaydin.application.playlist.dtos;

import java.util.List;

public class PlaylistDetailDto {
    private String id;
    private String name;
    private String description;
    private List<String> songTitles;

    public PlaylistDetailDto(String id, String name, String description, List<String> songTitles) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.songTitles = songTitles;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getSongTitles() { return songTitles; }
}
