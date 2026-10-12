package tr.com.huseyinaydin.application.playlist.dtos;

import java.util.List;

public class PlaylistDetailDto {
    private String id;
    private String name;
    private String description;
    private List<String> songTitles;

    public PlaylistDetailDto() {}

    public PlaylistDetailDto(String id, String name, String description, List<String> songTitles) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.songTitles = songTitles;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getSongTitles() { return songTitles; }
    public void setSongTitles(List<String> songTitles) { this.songTitles = songTitles; }
    public int getSongCount() { return songTitles != null ? songTitles.size() : 0; }
}
