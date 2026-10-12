package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.profile.dtos.UpdateProfileRequest;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;

public interface IProfileService {
    void updateProfile(UpdateProfileRequest request);
    UserProfileDto getProfile(String email);
    void updateProfileInfo(String currentEmail, String newEmail, String fullName, String username, String bio);
}
