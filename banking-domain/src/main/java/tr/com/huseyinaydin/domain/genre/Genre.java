package tr.com.huseyinaydin.domain.genre;

import tr.com.huseyinaydin.domain.common.Entity;
import java.util.UUID;

public class Genre extends Entity<UUID> {
    private String name;
    private String imageUrl;

    protected Genre() {
        super();
    }

    public Genre(String name, String imageUrl) {
        super();
        this.id = UUID.randomUUID();
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void update(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
}
