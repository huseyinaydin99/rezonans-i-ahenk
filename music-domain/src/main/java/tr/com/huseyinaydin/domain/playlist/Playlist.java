package tr.com.huseyinaydin.domain.playlist;

import java.util.UUID;

// Saf (Pure) Domain Entity
public class Playlist {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;

    protected Playlist() {}

    public Playlist(String name, String description, UUID ownerId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public UUID getOwnerId() { return ownerId; }
}
