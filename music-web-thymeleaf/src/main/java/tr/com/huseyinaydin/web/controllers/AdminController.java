package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;

import tr.com.huseyinaydin.application.itunes.commands.BatchImportSongsCommand;
import tr.com.huseyinaydin.application.itunes.dtos.BatchImportResultDto;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController extends BaseController {
    public AdminController(Mediator mediator) { super(mediator); }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')") // Deep Spring Security role check
    public ResponseEntity<String> getAdminDashboard() {
        return ResponseEntity.ok("Welcome to the Admin Dashboard. Secured by Spring Security.");
    }

    @PostMapping("/import/batch")
    public ResponseEntity<BatchImportResultDto> batchImportSongs(
            @RequestBody(required = false) BatchImportSongsCommand command) {
        BatchImportResultDto result = mediator.send(command != null ? command : new BatchImportSongsCommand(null, 5));
        return ResponseEntity.ok(result);
    }
}
