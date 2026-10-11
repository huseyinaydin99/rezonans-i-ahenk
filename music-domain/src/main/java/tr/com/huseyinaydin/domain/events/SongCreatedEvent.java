package tr.com.huseyinaydin.domain.events;

import tr.com.huseyinaydin.sharedkernel.events.DomainEvent;

import java.util.UUID;

public class SongCreatedEvent extends DomainEvent {
    private final UUID songId;
    private final String title;
    private final UUID artistId;
    private final String artistName;
    private final UUID genreId;
    private final String coverImageUrl;
    private final String tier;

    protected SongCreatedEvent() {
        super();
        this.songId = null;
        this.title = null;
        this.artistId = null;
        this.artistName = null;
        this.genreId = null;
        this.coverImageUrl = null;
        this.tier = null;
    }

    public SongCreatedEvent(UUID songId, String title, UUID artistId, UUID genreId) {
        this(songId, title, artistId, null, genreId, null, "Standard");
    }

    public SongCreatedEvent(UUID songId, String title, UUID artistId, String artistName, UUID genreId, String coverImageUrl, String tier) {
        super();
        this.songId = songId;
        this.title = title;
        this.artistId = artistId;
        this.artistName = artistName;
        this.genreId = genreId;
        this.coverImageUrl = coverImageUrl;
        this.tier = tier;
    }

    @Override
    public String getAggregateId() {
        return songId != null ? songId.toString() : "";
    }

    @Override
    public String getAggregateType() {
        return "Song";
    }

    public UUID getSongId() {
        return songId;
    }

    public String getTitle() {
        return title;
    }

    public UUID getArtistId() {
        return artistId;
    }

    public String getArtistName() {
        return artistName;
    }

    public UUID getGenreId() {
        return genreId;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public String getTier() {
        return tier;
    }
}
