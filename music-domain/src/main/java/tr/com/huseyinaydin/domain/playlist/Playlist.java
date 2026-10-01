package tr.com.huseyinaydin.domain.playlist;

import java.util.UUID;

public class Playlist {
    private UUID id;
    private String name;
    private String description;
    private UUID ownerId;

    protected Playlist() {}

    private Playlist(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.description = builder.description;
        this.ownerId = builder.ownerId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String description;
        private UUID ownerId;

        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder ownerId(UUID ownerId) { this.ownerId = ownerId; return this; }

        public Playlist build() {
            if (name == null || ownerId == null) throw new IllegalStateException("Eksik alanlar!");
            return new Playlist(this);
        }
    }
}
