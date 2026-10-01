package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.importdata.commands.ImportSongsCommand;

@RestController
@RequestMapping("/api/v1/import")
public class ImportController extends BaseController {

    public ImportController(Mediator mediator) { super(mediator); }

    @PostMapping("/songs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Integer> importSongs(@RequestBody String csvContent) {
        return ResponseEntity.ok(mediator.send(new ImportSongsCommand(csvContent)));
    }
}
