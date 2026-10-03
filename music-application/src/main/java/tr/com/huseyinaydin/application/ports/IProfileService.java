package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.profile.dtos.UpdateProfileRequest;

public interface IProfileService {
    void updateProfile(UpdateProfileRequest request);
}
