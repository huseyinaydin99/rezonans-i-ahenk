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

        public SongSnippet(Long songId, String title, String artist, String coverUrl) {
            this(songId, title, artist, coverUrl, "Basic", "Pop");
        }

        public SongSnippet(Long songId, String title, String artist, String coverUrl, String tier, String genre) {
            this.songId = songId;
            this.title = title;
            this.artist = artist;
            this.coverUrl = coverUrl;
            this.tier = tier != null ? tier : "Basic";
            this.genre = genre != null ? genre : "Pop";
        }

        public Long getSongId() { return songId; }
        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getCoverUrl() { return coverUrl; }
        public String getTier() { return tier; }
        public String getGenre() { return genre; }
    }
}
