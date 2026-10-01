package tr.com.huseyinaydin.application.role.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

public record CreateRoleCommand(String roleName) implements ICommand<Boolean> {
    @Component
    public static class Handler implements ICommandHandler<CreateRoleCommand, Boolean> {
        @Override
        public Boolean handle(CreateRoleCommand command) {
            // Logic to save the new OperationClaim/Role to the DB
            System.out.println("Yeni Rol (OperationClaim) oluşturuldu: " + command.roleName().toUpperCase());
            return true;
        }
    }
}
