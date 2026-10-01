package tr.com.huseyinaydin.domain.genre;

import java.util.UUID;

// Saf (Pure) Domain Entity
public class Genre {
    private UUID id;
    private String name;
    private String imageUrl;

    protected Genre() {}

    public Genre(String name, String imageUrl) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.imageUrl = imageUrl;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
