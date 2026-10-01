package tr.com.huseyinaydin.domain.genre;

import java.util.UUID;

public class Genre {
    private UUID id;
    private String name;
    private String imageUrl;

    protected Genre() {}

    private Genre(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID();
        this.name = builder.name;
        this.imageUrl = builder.imageUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String imageUrl;

        public Builder name(String name) { this.name = name; return this; }
        public Builder imageUrl(String imageUrl) { this.imageUrl = imageUrl; return this; }

        public Genre build() {
            if (name == null) throw new IllegalStateException("Tür adı zorunlu!");
            return new Genre(this);
        }
    }
}
