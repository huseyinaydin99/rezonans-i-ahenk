package tr.com.huseyinaydin.application.profile.dtos;

import java.io.InputStream;

public class UpdateProfileRequest {
    private String userId;
    private String bio;
    private String currentPassword;
    private String newPassword;
    
    // Profil resmi için stream ve metadata
    private InputStream profileImageStream;
    private String imageFileName;
    private long imageSize;
    private String imageContentType;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public InputStream getProfileImageStream() { return profileImageStream; }
    public void setProfileImageStream(InputStream profileImageStream) { this.profileImageStream = profileImageStream; }
    public String getImageFileName() { return imageFileName; }
    public void setImageFileName(String imageFileName) { this.imageFileName = imageFileName; }
    public long getImageSize() { return imageSize; }
    public void setImageSize(long imageSize) { this.imageSize = imageSize; }
    public String getImageContentType() { return imageContentType; }
    public void setImageContentType(String imageContentType) { this.imageContentType = imageContentType; }
}
