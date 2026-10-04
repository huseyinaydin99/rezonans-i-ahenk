package tr.com.huseyinaydin.application.home.dtos;

import java.util.List;

public class HomeDashboardDto {
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

    public static class SongSnippet {
        private String title;
        private String artist;
        private String coverUrl;

        public SongSnippet(String title, String artist, String coverUrl) {
            this.title = title;
            this.artist = artist;
            this.coverUrl = coverUrl;
        }

        public String getTitle() { return title; }
        public String getArtist() { return artist; }
        public String getCoverUrl() { return coverUrl; }
    }
}
