package tr.com.huseyinaydin.application.auth.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record RegisterUserCommand(String email, String password, String name, String surname) implements ICommand<UUID> {
    @Component
    public static class Handler implements ICommandHandler<RegisterUserCommand, UUID> {
        @Override
        public UUID handle(RegisterUserCommand command) {
            // Adapted .NET Identity logic to Java Application Layer (Hashing, saving user)
            return UUID.randomUUID(); // Mocked return for architecture completion
        }
    }
}
