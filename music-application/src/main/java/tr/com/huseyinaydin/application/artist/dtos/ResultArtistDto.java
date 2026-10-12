package tr.com.huseyinaydin.application.artist.dtos;

public class ResultArtistDto {
    private String id;
    private String name;
    private String imageUrl;
    private long monthlyListeners;
    private boolean verified;

    public ResultArtistDto(String id, String name, String imageUrl, long monthlyListeners, boolean verified) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.monthlyListeners = monthlyListeners;
        this.verified = verified;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getImageUrl() { return imageUrl; }
    public long getMonthlyListeners() { return monthlyListeners; }
    public boolean isVerified() { return verified; }

    public String getFormattedMonthlyListeners() {
        return String.format(new java.util.Locale("tr", "TR"), "%,d", monthlyListeners) + " AYLIK DİNLENİYİCİ";
    }
}
