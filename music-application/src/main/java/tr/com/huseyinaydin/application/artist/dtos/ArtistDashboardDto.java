package tr.com.huseyinaydin.application.artist.dtos;

public class ArtistDashboardDto {
    private String artistId;
    private String artistName;
    private int totalFollowers;
    private long totalStreams; // Toplam dinlenme sayısı
    private int totalSongs;

    public ArtistDashboardDto(String artistId, String artistName, int totalFollowers, long totalStreams, int totalSongs) {
        this.artistId = artistId;
        this.artistName = artistName;
        this.totalFollowers = totalFollowers;
        this.totalStreams = totalStreams;
        this.totalSongs = totalSongs;
    }

    public String getArtistId() { return artistId; }
    public String getArtistName() { return artistName; }
    public int getTotalFollowers() { return totalFollowers; }
    public long getTotalStreams() { return totalStreams; }
    public int getTotalSongs() { return totalSongs; }
}
