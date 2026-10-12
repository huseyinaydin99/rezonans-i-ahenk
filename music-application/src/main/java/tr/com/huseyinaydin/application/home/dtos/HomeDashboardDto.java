package tr.com.huseyinaydin.application.home.dtos;

import java.io.Serializable;
import java.util.List;

public class HomeDashboardDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<SongSnippet> topGlobalSongs;
    private List<SongSnippet> newReleases;
    private List<SongSnippet> personalizedRecommendations;

    public HomeDashboardDto(List<SongSnippet> topGlobalSongs, List<SongSnippet> newReleases, List<SongSnippet> personalizedRecommendations) {
        this.topGlobalSongs = topGlobalSongs;
        this.newReleases = newReleases;
        this.personalizedRecommendations = personalizedRecommendations;
    }

    public List<SongSnippet> getTopGlobalSongs() { return topGlobalSongs; }
    public List<SongSnippet> getNewReleases() { return newReleases; }
    public List<SongSnippet> getPersonalizedRecommendations() { return personalizedRecommendations; }

    public static class SongSnippet implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long songId;
        private String title;
        private String artist;
        private String coverUrl;
        private String tier;
        private String genre;
        private Long playCount;
        private String matchReason;

        public SongSnippet(Long songId, String title, String artist, String coverUrl) {
            this(songId, title, artist, coverUrl, "Basic", "Pop", 0L, null);
        }

        public SongSnippet(Long songId, String title, String artist, String coverUrl, String tier, String genre) {
            this(songId, title, artist, coverUrl, tier, genre, 0L, null);
        }

        public SongSnippet(Long songId, String title, String artist, String coverUrl, String tier, String genre, Long playCount, String matchReason) {
            this.songId = songId;
            this.title = title;
            this.artist = artist;
            this.coverUrl = coverUrl;
            this.tier = tier != null ? tier : "Basic";
            this.genre = genre != null ? genre : "Pop";
            this.playCount = playCount != null ? playCount : 0L;
            this.matchReason = matchReason;
        }

        public Long getSongId() { return songId; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getCoverUrl() { return coverUrl; }
        public String getTier() { return tier; }
        public String getGenre() { return genre; }
        public Long getPlayCount() { return playCount; }
        public String getMatchReason() { return matchReason; }
    }
}
