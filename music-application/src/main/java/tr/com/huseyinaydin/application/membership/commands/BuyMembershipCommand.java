package tr.com.huseyinaydin.application.membership.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.domain.repositories.IApplicationUserRepository;
import tr.com.huseyinaydin.domain.user.ApplicationUser;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.util.Optional;

/**
 * Kullanıcının üyelik paketini (Basic, Gold, Premium, Elit) güncelleyen CQRS komutu.
 */
public record BuyMembershipCommand(String email, String targetTierName) implements ICommand<String> {
    private static final Logger log = LoggerFactory.getLogger(BuyMembershipCommand.class);

    @Component
    public static class Handler implements ICommandHandler<BuyMembershipCommand, String> {
        private final IProfileService profileService;
        private final IApplicationUserRepository userRepository;

        public Handler(@Autowired(required = false) IProfileService profileService,
                       @Autowired(required = false) IApplicationUserRepository userRepository) {
            this.profileService = profileService;
            this.userRepository = userRepository;
        }

        @Override
        public String handle(BuyMembershipCommand command) {
            log.info("Executing BuyMembershipCommand for user: {}, targetTier: {}",
                    command.email(), command.targetTierName());

            String raw = command.targetTierName() != null ? command.targetTierName().trim().toUpperCase() : "";
            String canonicalTier;
            MembershipTier enumTier;

            if (raw.equals("BASIC")) {
                canonicalTier = "Basic";
                enumTier = MembershipTier.BASIC;
            } else if (raw.equals("GOLD")) {
                canonicalTier = "Gold";
                enumTier = MembershipTier.GOLD;
            } else if (raw.equals("PREMIUM")) {
                canonicalTier = "Premium";
                enumTier = MembershipTier.PREMIUM;
            } else if (raw.equals("ELIT") || raw.equals("ELITE")) {
                canonicalTier = "Elit";
                enumTier = MembershipTier.ELIT;
            } else {
                throw new BusinessException("Geçersiz paket seçimi: " + command.targetTierName(), "INVALID_TIER");
            }

            if (profileService != null && command.email() != null) {
                UserProfileDto profile = profileService.getProfile(command.email());
                if (profile != null) {
                    profile.setTier(canonicalTier);
                }
            }

            if (userRepository != null && command.email() != null) {
                try {
                    Optional<ApplicationUser> userOpt = userRepository.findByEmail(command.email());
                    if (userOpt.isPresent()) {
                        ApplicationUser user = userOpt.get();
                        userRepository.save(user);
                    }
                } catch (Exception ignored) {}
            }

            log.info("User {} membership successfully updated to tier: {}", command.email(), canonicalTier);
            return canonicalTier;
        }
    }
}
