package tr.com.huseyinaydin.web.beans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.annotation.PostConstruct;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IAdminService;
import tr.com.huseyinaydin.application.admin.dtos.UserRoleDto;

import java.io.Serializable;
import java.util.List;

@Component
@SessionScope
public class AdminBean implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(AdminBean.class);


    @Autowired
    private IAdminService adminService;

    private List<UserRoleDto> userList;
    private String selectedRole;

    @PostConstruct
    public void init() {
        loadUsers();
    }

    public void loadUsers() {
        this.userList = adminService.getAllUsers();
    }

    public void updateRole(UserRoleDto user) {
        if (selectedRole != null && !selectedRole.isEmpty()) {
            adminService.assignRole(user.getUserId(), selectedRole);
            user.setCurrentRole(selectedRole);
            log.info("[JSF AJAX] Rol güncelleme işlemi başarılı.");
        }
    }

    public void toggleBan(UserRoleDto user) {
        boolean newStatus = !user.isBanned();
        adminService.toggleUserBanStatus(user.getUserId(), newStatus);
        user.setBanned(newStatus);
        log.info("[JSF AJAX] Kullanıcı yasaklama/kaldırma işlemi başarılı.");
    }

    // Getters and Setters
    public List<UserRoleDto> getUserList() { return userList; }
    public String getSelectedRole() { return selectedRole; }
    public void setSelectedRole(String selectedRole) { this.selectedRole = selectedRole; }
}
