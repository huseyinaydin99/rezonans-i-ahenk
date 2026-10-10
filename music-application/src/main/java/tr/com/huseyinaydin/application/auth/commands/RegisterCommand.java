package tr.com.huseyinaydin.application.auth.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto;
import tr.com.huseyinaydin.application.ports.IRegisterService;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

public record RegisterCommand(String username, String email, String password, boolean isArtist) implements ICommand<Boolean> {
    
    @Component
    public static class Handler implements ICommandHandler<RegisterCommand, Boolean> {
        
        private final IRegisterService registerService;

        public Handler(IRegisterService registerService) {
            this.registerService = registerService;
        }

        @Override
        public Boolean handle(RegisterCommand command) {
            RegisterRequestDto req = new RegisterRequestDto();
            req.setUsername(command.username());
            req.setEmail(command.email());
            req.setPassword(command.password());
            req.setFullName(command.username());
            req.setArtistAccount(command.isArtist());
            
            registerService.registerNewUser(req);
            return true;
        }
    }
}
