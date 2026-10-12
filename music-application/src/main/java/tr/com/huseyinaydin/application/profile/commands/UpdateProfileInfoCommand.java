package tr.com.huseyinaydin.application.profile.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.application.ports.IProfileService;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.util.Locale;

/**
 * Kullanıcı profil bilgilerini güncelleyen ve e-posta değiştiğinde kullanıcı ilişkilerini taşıyan CQRS komutu.
 */
public record UpdateProfileInfoCommand(
        String currentEmail,
        String newEmail,
        String fullName,
        String username,
        String bio
) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(UpdateProfileInfoCommand.class);

    @Component
    public static class Handler implements ICommandHandler<UpdateProfileInfoCommand, Boolean> {
        private final IProfileService profileService;
        private final IPlaylistService playlistService;

        public Handler(@Autowired(required = false) IProfileService profileService,
                       @Autowired(required = false) IPlaylistService playlistService) {
            this.profileService = profileService;
            this.playlistService = playlistService;
        }

        @Override
        public Boolean handle(UpdateProfileInfoCommand command) {
            log.info("Executing UpdateProfileInfoCommand for currentEmail: {} -> newEmail: {}",
                    command.currentEmail(), command.newEmail());

            if (profileService != null) {
                profileService.updateProfileInfo(
                        command.currentEmail(),
                        command.newEmail(),
                        command.fullName(),
                        command.username(),
                        command.bio()
                );
            }

            String normalizedCurrent = (command.currentEmail() != null)
                    ? command.currentEmail().trim().toLowerCase(Locale.ROOT) : "";
            String normalizedNew = (command.newEmail() != null)
                    ? command.newEmail().trim().toLowerCase(Locale.ROOT) : "";

            if (!normalizedCurrent.isEmpty() && !normalizedNew.isEmpty() && !normalizedCurrent.equalsIgnoreCase(normalizedNew)) {
                if (playlistService != null) {
                    playlistService.migrateUser(normalizedCurrent, normalizedNew);
                    log.info("Migrated playlist store from {} to {}", normalizedCurrent, normalizedNew);
                }
            }

            return true;
        }
    }
}
