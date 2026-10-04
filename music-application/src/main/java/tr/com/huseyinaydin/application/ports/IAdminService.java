package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.admin.dtos.UserRoleDto;
import java.util.List;

public interface IAdminService {
    List<UserRoleDto> getAllUsers();
    void assignRole(String userId, String newRole);
    void toggleUserBanStatus(String userId, boolean status);
}
