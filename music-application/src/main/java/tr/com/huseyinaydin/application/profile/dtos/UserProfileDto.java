package tr.com.huseyinaydin.application.profile.dtos;

public class UserProfileDto {
    private String fullName;
    private String username;
    private String email;
    private String bio;
    private String tier;
    private int playlistCount;
    private int likeCount;
    private int credits;

    public UserProfileDto() {}

    public UserProfileDto(String fullName, String username, String email, String bio, String tier) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.tier = tier != null ? tier : "Premium";
        this.credits = 2500;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getTier() { return tier; }
    public void setTier(String tier) { this.tier = tier; }

    public int getPlaylistCount() { return playlistCount; }
    public void setPlaylistCount(int playlistCount) { this.playlistCount = playlistCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getInitials() {
        if (fullName == null || fullName.isBlank()) return "HU";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
