package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.membership.commands.UpgradeMembershipCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/membership")
@PreAuthorize("isAuthenticated()")
public class MembershipController extends BaseController {

    public MembershipController(Mediator mediator) { super(mediator); }

    @PostMapping("/upgrade")
    public ResponseEntity<Boolean> upgradeTier(@RequestBody UpgradeMembershipCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }
}
