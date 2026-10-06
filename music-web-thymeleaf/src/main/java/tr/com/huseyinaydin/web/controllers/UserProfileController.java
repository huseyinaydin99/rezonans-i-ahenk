package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.user.commands.UpdateUserProfileCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users/profile")
public class UserProfileController extends BaseController {
    public UserProfileController(Mediator mediator) { super(mediator); }

    @PutMapping("/{id}")
    public ResponseEntity<Boolean> updateProfile(@PathVariable UUID id, @RequestBody UpdateUserProfileCommand command) {
        if (!command.userId().equals(id)) throw new IllegalArgumentException("ID mismatch");
        return ResponseEntity.ok(mediator.send(command));
    }
}
