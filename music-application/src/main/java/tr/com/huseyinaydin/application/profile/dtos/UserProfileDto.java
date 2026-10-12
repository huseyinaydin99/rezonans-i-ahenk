package tr.com.huseyinaydin.application.profile.dtos;

public class UserProfileDto {
    private String fullName;
    private String username;
    private String email;
    private String bio;
    private String tier;
    private int playlistCount;
    private int likeCount;
    private int listenCount = 25;
    private int credits = 2500;
    private String avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80";

    public UserProfileDto() {}

    public UserProfileDto(String fullName, String username, String email, String bio, String tier) {
        this(fullName, username, email, bio, tier, "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80", 25);
    }

    public UserProfileDto(String fullName, String username, String email, String bio, String tier, String avatarUrl, int listenCount) {
        this.fullName = fullName;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.tier = tier != null ? tier : "Premium";
        this.credits = 2500;
        this.avatarUrl = (avatarUrl != null && !avatarUrl.isBlank()) ? avatarUrl : "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80";
        this.listenCount = listenCount;
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

    public int getListenCount() { return listenCount; }
    public void setListenCount(int listenCount) { this.listenCount = listenCount; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    public String getAvatarUrl() {
        if (avatarUrl != null && !avatarUrl.isBlank()) return avatarUrl;
        return "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=300&auto=format&fit=crop&q=80";
    }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getInitials() {
        if (fullName == null || fullName.isBlank()) return "AS";
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        }
        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }
}
