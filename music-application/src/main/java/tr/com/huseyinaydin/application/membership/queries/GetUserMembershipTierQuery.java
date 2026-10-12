package tr.com.huseyinaydin.application.membership.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.domain.repositories.IApplicationUserRepository;
import tr.com.huseyinaydin.domain.user.ApplicationUser;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Optional;

/**
 * Kullanıcının mevcut üyelik kademesini (Basic, Gold, Premium, Elit) getiren CQRS sorgusu.
 */
public record GetUserMembershipTierQuery(String email) implements IQuery<String> {
    private static final Logger log = LoggerFactory.getLogger(GetUserMembershipTierQuery.class);

    @Component
    public static class Handler implements IQueryHandler<GetUserMembershipTierQuery, String> {
        private final IProfileService profileService;
        private final IApplicationUserRepository userRepository;

        public Handler(@Autowired(required = false) IProfileService profileService,
                       @Autowired(required = false) IApplicationUserRepository userRepository) {
            this.profileService = profileService;
            this.userRepository = userRepository;
        }

        @Override
        public String handle(GetUserMembershipTierQuery query) {
            log.info("Executing GetUserMembershipTierQuery for email: {}", query.email());
            String tier = null;

            if (profileService != null && query.email() != null) {
                try {
                    UserProfileDto profile = profileService.getProfile(query.email());
                    if (profile != null && profile.getTier() != null && !profile.getTier().isBlank()) {
                        tier = profile.getTier();
                    }
                } catch (Exception ignored) {}
            }

            if ((tier == null || tier.isBlank()) && userRepository != null && query.email() != null) {
                try {
                    Optional<ApplicationUser> userOpt = userRepository.findByEmail(query.email());
                    if (userOpt.isPresent() && userOpt.get().getRole() != null) {
                        tier = userOpt.get().getRole().name();
                    }
                } catch (Exception ignored) {}
            }

            if (tier == null || tier.isBlank()) {
                tier = "Basic";
            }

            // Normalizasyon: Baş harfi büyük, geri kalanı küçük (Basic, Gold, Premium, Elit)
            String normalized = tier.trim().toUpperCase();
            if (normalized.equals("ELITE") || normalized.equals("ELIT")) return "Elit";
            if (normalized.equals("PREMIUM")) return "Premium";
            if (normalized.equals("GOLD")) return "Gold";
            return "Basic";
        }
    }
}
