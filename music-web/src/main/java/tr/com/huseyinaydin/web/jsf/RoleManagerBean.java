package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.role.commands.CreateRoleCommand;
import tr.com.huseyinaydin.application.role.commands.AssignRoleToUserCommand;

import java.util.UUID;

@Named
@RequestScoped
public class RoleManagerBean {

    @Autowired
    private Mediator mediator;

    private String newRoleName;
    private String targetUserId;
    private String selectedRoleToAssign;
    private String systemMessage;

    public void createRole() {
        if (newRoleName != null && !newRoleName.trim().isEmpty()) {
            mediator.send(new CreateRoleCommand(newRoleName));
            this.systemMessage = "Rol başarıyla oluşturuldu: " + newRoleName;
            this.newRoleName = "";
        }
    }

    public void assignRole() {
        if (targetUserId != null && selectedRoleToAssign != null) {
            try {
                UUID uId = UUID.fromString(targetUserId);
                mediator.send(new AssignRoleToUserCommand(uId, selectedRoleToAssign));
                this.systemMessage = "Kullanıcıya yetki başarıyla atandı.";
            } catch (Exception e) {
                this.systemMessage = "Geçersiz Kullanıcı ID'si.";
            }
        }
    }

    // Getters and Setters
    public String getNewRoleName() { return newRoleName; }
    public void setNewRoleName(String newRoleName) { this.newRoleName = newRoleName; }
    public String getTargetUserId() { return targetUserId; }
    public void setTargetUserId(String targetUserId) { this.targetUserId = targetUserId; }
    public String getSelectedRoleToAssign() { return selectedRoleToAssign; }
    public void setSelectedRoleToAssign(String selectedRoleToAssign) { this.selectedRoleToAssign = selectedRoleToAssign; }
    public String getSystemMessage() { return systemMessage; }
}
