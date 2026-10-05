package tr.com.huseyinaydin.web.jsf;

import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.auth.commands.RegisterUserCommand;

@Component
@RequestScope
public class AuthBean {
    @Autowired
    private Mediator mediator;

    private String email;
    private String password;
    private String name;
    private String surname;

    public String register() {
        mediator.send(new RegisterUserCommand(email, password, name, surname));
        return "/auth/login.xhtml?faces-redirect=true";
    }
    
    // Getters and Setters omitted for brevity
}
