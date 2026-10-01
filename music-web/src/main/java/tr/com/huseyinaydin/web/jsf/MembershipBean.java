package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.membership.commands.UpgradeMembershipCommand;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import java.util.UUID;

@Named
@RequestScoped
public class MembershipBean {

    @Autowired
    private Mediator mediator;

    private String targetTier;
    private String creditCardToken = "tok_simulated_123";
    private String systemMessage;
    private boolean upgradeSuccess = false;

    public void processUpgrade(String tier) {
        this.targetTier = tier;
        try {
            // Mocking current user UUID
            UUID currentUserId = UUID.randomUUID(); 
            Boolean result = mediator.send(new UpgradeMembershipCommand(currentUserId, targetTier, creditCardToken));
            
            if (result) {
                this.upgradeSuccess = true;
                this.systemMessage = "Tebrikler! Müzik deneyiminiz " + tier + " paketine yükseltildi.";
            }
        } catch (BusinessException ex) {
            this.upgradeSuccess = false;
            this.systemMessage = ex.getMessage();
        } catch (Exception ex) {
            this.upgradeSuccess = false;
            this.systemMessage = "İşlem sırasında beklenmedik bir hata oluştu.";
        }
    }

    public String getTargetTier() { return targetTier; }
    public void setTargetTier(String targetTier) { this.targetTier = targetTier; }
    public String getSystemMessage() { return systemMessage; }
    public boolean isUpgradeSuccess() { return upgradeSuccess; }
}
