package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.itunes.queries.SearchItunesQuery;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;

import tr.com.huseyinaydin.application.itunes.commands.BatchImportSongsCommand;
import tr.com.huseyinaydin.application.itunes.dtos.BatchImportResultDto;

@RestController
@RequestMapping("/api/v1/external/itunes")
public class ItunesController extends BaseController {

    public ItunesController(Mediator mediator) { super(mediator); }

    @GetMapping("/search")
    public ResponseEntity<ItunesResponseDto> searchItunes(
            @RequestParam String term, 
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(mediator.query(new SearchItunesQuery(term, limit)));
    }

    @PostMapping("/import/batch")
    public ResponseEntity<BatchImportResultDto> batchImport(
            @RequestBody(required = false) BatchImportSongsCommand command) {
        BatchImportResultDto result = mediator.send(command != null ? command : new BatchImportSongsCommand(null, 5));
        return ResponseEntity.ok(result);
    }
}
