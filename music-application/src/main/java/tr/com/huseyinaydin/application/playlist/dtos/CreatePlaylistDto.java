package tr.com.huseyinaydin.application.playlist.dtos;

public class CreatePlaylistDto {
    private String name;
    private String description;
    private String userId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
