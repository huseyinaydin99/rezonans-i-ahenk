package tr.com.huseyinaydin.application.artist.dtos;

import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.util.List;

public class ArtistDetailDto {
    private String id;
    private String name;
    private String imageUrl;
    private String bio;
    private long monthlyListeners;
    private boolean verified;
    private List<SongResponse> popularSongs;

    public ArtistDetailDto(String id, String name, String imageUrl, String bio, long monthlyListeners, boolean verified, List<SongResponse> popularSongs) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.monthlyListeners = monthlyListeners;
        this.verified = verified;
        this.popularSongs = popularSongs;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public String getBio() { return bio; }
    public long getMonthlyListeners() { return monthlyListeners; }
    public boolean isVerified() { return verified; }
    public List<SongResponse> getPopularSongs() { return popularSongs; }
}
