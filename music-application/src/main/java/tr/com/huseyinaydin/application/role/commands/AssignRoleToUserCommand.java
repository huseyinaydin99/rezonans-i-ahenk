package tr.com.huseyinaydin.application.role.commands;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record AssignRoleToUserCommand(UUID userId, String roleName) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(AssignRoleToUserCommand.class);

    @Component
    public static class Handler implements ICommandHandler<AssignRoleToUserCommand, Boolean> {
        @Override
        public Boolean handle(AssignRoleToUserCommand command) {
            // Logic to map UserOperationClaim/UserRole in the DB
            log.info("Kullanıcı " + command.userId() + " için " + command.roleName() + " yetkisi tanımlandı.");
            return true;
        }
    }
}
