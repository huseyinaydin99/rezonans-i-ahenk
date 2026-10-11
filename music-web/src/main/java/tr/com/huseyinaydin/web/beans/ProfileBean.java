package tr.com.huseyinaydin.web.beans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.Part; // JSF File Upload için
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.profile.dtos.UpdateProfileRequest;
import java.io.IOException;

@Component
@RequestScope
public class ProfileBean {
    private static final Logger log = LoggerFactory.getLogger(ProfileBean.class);


    @Autowired
    private IProfileService profileService;

    private String bio;
    private String currentPassword;
    private String newPassword;
    private Part profileImage; // JSF <h:inputFile> bileşeniyle eşleşir

    public void saveProfile() {
        try {
            UpdateProfileRequest request = new UpdateProfileRequest();
            request.setUserId("current-logged-in-user-id"); // Gerçekte SecurityContext'ten alınır
            request.setBio(bio);
            request.setCurrentPassword(currentPassword);
            request.setNewPassword(newPassword);

            if (profileImage != null && profileImage.getSize() > 0) {
                request.setProfileImageStream(profileImage.getInputStream());
                request.setImageFileName(profileImage.getSubmittedFileName());
                request.setImageSize(profileImage.getSize());
                request.setImageContentType(profileImage.getContentType());
            }

            profileService.updateProfile(request);
            
            // Başarılı mesajı eklenebilir (FacesContext.addMessage)
            log.info("[JSF] Profil kaydetme işlemi arayüzden başarıyla tetiklendi.");
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and Setters
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    public String getCurrentPassword() { return currentPassword; }
    public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    public Part getProfileImage() { return profileImage; }
    public void setProfileImage(Part profileImage) { this.profileImage = profileImage; }
}
