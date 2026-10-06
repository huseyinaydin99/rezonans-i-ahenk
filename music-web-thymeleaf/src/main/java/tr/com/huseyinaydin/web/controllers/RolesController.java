package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.role.commands.CreateRoleCommand;
import tr.com.huseyinaydin.application.role.commands.AssignRoleToUserCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/roles")
@PreAuthorize("hasRole('ADMIN')") // Tüm rol işlemleri sadece yöneticilere özeldir
public class RolesController extends BaseController {

    public RolesController(Mediator mediator) { super(mediator); }

    @PostMapping
    public ResponseEntity<Boolean> createRole(@RequestBody CreateRoleCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }

    @PostMapping("/assign")
    public ResponseEntity<Boolean> assignRole(@RequestBody AssignRoleToUserCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }
}
