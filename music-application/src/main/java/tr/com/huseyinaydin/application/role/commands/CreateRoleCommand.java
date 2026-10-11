package tr.com.huseyinaydin.application.role.commands;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

public record CreateRoleCommand(String roleName) implements ICommand<Boolean> {
    private static final Logger log = LoggerFactory.getLogger(CreateRoleCommand.class);

    @Component
    public static class Handler implements ICommandHandler<CreateRoleCommand, Boolean> {
        @Override
        public Boolean handle(CreateRoleCommand command) {
            // Logic to save the new OperationClaim/Role to the DB
            log.info("Yeni Rol (OperationClaim) oluşturuldu: " + command.roleName().toUpperCase());
            return true;
        }
    }
}
