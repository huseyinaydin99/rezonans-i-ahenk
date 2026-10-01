package tr.com.huseyinaydin.application.user.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record UpdateUserProfileCommand(UUID userId, String bio, String profileImageUrl) implements ICommand<Boolean> {
    
    @Component
    public static class Handler implements ICommandHandler<UpdateUserProfileCommand, Boolean> {
        @Override
        public Boolean handle(UpdateUserProfileCommand command) {
            // Logic to fetch user, update bio and imageUrl, and save.
            // Ported from .NET Identity profile updates.
            return true;
        }
    }
}
