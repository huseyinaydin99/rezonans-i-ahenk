package tr.com.huseyinaydin.application.profile.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.application.profile.dtos.UserProfileDto;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

/**
 * Kullanıcı profil bilgilerini getiren CQRS sorgusu.
 */
public record GetUserProfileQuery(String email) implements IQuery<UserProfileDto> {
    private static final Logger log = LoggerFactory.getLogger(GetUserProfileQuery.class);

    @Component
    public static class Handler implements IQueryHandler<GetUserProfileQuery, UserProfileDto> {
        private final IProfileService profileService;

        public Handler(@Autowired(required = false) IProfileService profileService) {
            this.profileService = profileService;
        }

        @Override
        public UserProfileDto handle(GetUserProfileQuery query) {
            log.info("Executing GetUserProfileQuery for email: {}", query.email());
            if (profileService != null && query.email() != null) {
                return profileService.getProfile(query.email());
            }
            return new UserProfileDto("Kullanıcı", "user", query.email(), "Müzik tutkunu.", "Basic");
        }
    }
}
