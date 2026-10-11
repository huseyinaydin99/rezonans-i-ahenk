package tr.com.huseyinaydin.application.membership.commands;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record UpgradeMembershipCommand(UUID userId, String targetTierName, String creditCardToken) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(UpgradeMembershipCommand.class);

    
    @Component
    public static class Handler implements ICommandHandler<UpgradeMembershipCommand, Boolean> {
        
        @Override
        public Boolean handle(UpgradeMembershipCommand command) {
            MembershipTier targetTier = MembershipTier.valueOf(command.targetTierName().toUpperCase());
            
            // Assume we fetched the user from the repository. Mocking the current tier as BASIC.
            MembershipTier currentTier = MembershipTier.BASIC; 
            
            if (currentTier.ordinal() >= targetTier.ordinal()) {
                throw new BusinessException("ERR", "Zaten bu pakete veya daha üst bir pakete sahipsiniz.");
            }

            // Simulating Payment Gateway Integration
            log.info("Ödeme alınıyor... Kredi Kartı Token: " + command.creditCardToken());
            boolean paymentSuccess = true; // Simulated

            if (!paymentSuccess) {
                throw new BusinessException("ERR", "Ödeme işlemi başarısız oldu. Lütfen tekrar deneyin.");
            }

            // user.setMembershipTier(targetTier);
            // userRepository.update(user);
            log.info("Kullanıcı " + command.userId() + " paketi başarıyla " + targetTier.name() + " seviyesine yükseltildi.");

            return true;
        }
    }
}
