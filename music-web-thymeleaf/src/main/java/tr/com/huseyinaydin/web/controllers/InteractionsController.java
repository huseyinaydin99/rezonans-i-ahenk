package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.interaction.commands.LikeSongCommand;
import tr.com.huseyinaydin.application.interaction.commands.RecordListeningHistoryCommand;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/interactions")
public class InteractionsController extends BaseController {
    public InteractionsController(Mediator mediator) { super(mediator); }

    @PostMapping("/like")
    public ResponseEntity<UUID> likeSong(@RequestBody LikeSongCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }

    @PostMapping("/history")
    public ResponseEntity<UUID> recordHistory(@RequestBody RecordListeningHistoryCommand command) {
        return ResponseEntity.ok(mediator.send(command));
    }
}
