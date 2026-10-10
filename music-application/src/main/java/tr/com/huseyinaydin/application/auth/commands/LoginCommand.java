package tr.com.huseyinaydin.application.auth.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.auth.dtos.LoginRequestDto;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;
import tr.com.huseyinaydin.application.ports.ILoginService;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

public record LoginCommand(String email, String password) implements ICommand<ResponseTokenDto> {
    
    @Component
    public static class Handler implements ICommandHandler<LoginCommand, ResponseTokenDto> {
        
        private final ILoginService loginService;

        public Handler(ILoginService loginService) {
            this.loginService = loginService;
        }

        @Override
        public ResponseTokenDto handle(LoginCommand command) {
            LoginRequestDto req = new LoginRequestDto();
            req.setEmail(command.email());
            req.setPassword(command.password());
            return loginService.authenticate(req);
        }
    }
}
