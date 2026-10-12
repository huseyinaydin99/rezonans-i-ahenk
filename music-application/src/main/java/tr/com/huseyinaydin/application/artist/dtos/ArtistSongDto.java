package tr.com.huseyinaydin.application.artist.dtos;

import java.io.Serializable;

public class ArtistSongDto implements Serializable {
    private Long songId;
    private String id;
    private String title;
    private String coverImageUrl;
    private String audioUrl;
    private String duration;
    private long playCount;
    private String tier;
    private int tierLevel;
    private String artistName;
    private String genreName;

    public ArtistSongDto() {}

    public ArtistSongDto(Long songId, String title, String coverImageUrl, String audioUrl,
                         String duration, long playCount, String tier, int tierLevel,
                         String artistName, String genreName) {
        this.songId = songId;
        this.id = songId != null ? String.valueOf(songId) : "";
        this.title = title;
        this.coverImageUrl = coverImageUrl;
        this.audioUrl = audioUrl;
        this.duration = duration;
        this.playCount = playCount;
        this.tier = tier != null ? tier : "Basic";
        this.tierLevel = tierLevel;
        this.artistName = artistName;
        this.genreName = genreName;
    }

    public Long getSongId() { return songId; }
    public void setSongId(Long songId) { 
        this.songId = songId;
        if (songId != null) this.id = String.valueOf(songId);
    }

    public String getId() { return id != null ? id : (songId != null ? String.valueOf(songId) : ""); }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getCover() { return coverImageUrl; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public long getPlayCount() { return playCount; }
    public void setPlayCount(long playCount) { this.playCount = playCount; }

    public String getFormattedPlayCount() {
        return String.format(new java.util.Locale("tr", "TR"), "%,d", playCount);
    }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public int getTierLevel() { return tierLevel; }
    public void setTierLevel(int tierLevel) { this.tierLevel = tierLevel; }

    public String getArtistName() { return artistName; }
    public void setArtistName(String artistName) { this.artistName = artistName; }

    public String getGenreName() { return genreName; }
    public void setGenreName(String genreName) { this.genreName = genreName; }
}
