package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.auth.commands.RegisterUserCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController {
    public AuthController(Mediator mediator) { super(mediator); }

    @PostMapping("/register")
    public ResponseEntity<UUID> register(@RequestBody RegisterUserCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }
}
