package tr.com.huseyinaydin.application.artist.dtos;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class ArtistDetailDto implements Serializable {
    private String id;
    private String name;
    private String imageUrl;
    private String bio;
    private long monthlyListeners;
    private boolean verified;
    private List<ArtistSongDto> popularSongs;

    public ArtistDetailDto() {
        this.popularSongs = Collections.emptyList();
    }

    public ArtistDetailDto(String id, String name, String imageUrl, String bio,
                           long monthlyListeners, boolean verified,
                           List<ArtistSongDto> popularSongs) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.bio = bio;
        this.monthlyListeners = monthlyListeners;
        this.verified = verified;
        this.popularSongs = popularSongs != null ? popularSongs : Collections.emptyList();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public long getMonthlyListeners() { return monthlyListeners; }
    public void setMonthlyListeners(long monthlyListeners) { this.monthlyListeners = monthlyListeners; }

    public String getFormattedMonthlyListeners() {
        return String.format(new java.util.Locale("tr", "TR"), "%,d", monthlyListeners) + " AYLIK DİNLENİCİ";
    }

    public boolean isVerified() { return verified; }
    public boolean getIsVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setIsVerified(boolean isVerified) { this.verified = isVerified; }

    public List<ArtistSongDto> getPopularSongs() { return popularSongs; }
    public void setPopularSongs(List<ArtistSongDto> popularSongs) { 
        this.popularSongs = popularSongs != null ? popularSongs : Collections.emptyList(); 
    }

    public List<ArtistSongDto> getSongs() { return popularSongs; }
}
