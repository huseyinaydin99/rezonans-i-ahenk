package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.auth.commands.RegisterUserCommand;

@Named
@RequestScoped
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
