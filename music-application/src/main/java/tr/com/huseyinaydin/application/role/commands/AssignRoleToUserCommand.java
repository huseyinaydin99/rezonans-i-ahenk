package tr.com.huseyinaydin.application.role.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record AssignRoleToUserCommand(UUID userId, String roleName) implements ICommand<Boolean> {
    @Component
    public static class Handler implements ICommandHandler<AssignRoleToUserCommand, Boolean> {
        @Override
        public Boolean handle(AssignRoleToUserCommand command) {
            // Logic to map UserOperationClaim/UserRole in the DB
            System.out.println("Kullanıcı " + command.userId() + " için " + command.roleName() + " yetkisi tanımlandı.");
            return true;
        }
    }
}
