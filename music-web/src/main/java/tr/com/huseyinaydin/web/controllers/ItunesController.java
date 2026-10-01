package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.itunes.queries.SearchItunesQuery;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;

@RestController
@RequestMapping("/api/v1/external/itunes")
public class ItunesController extends BaseController {

    public ItunesController(Mediator mediator) { super(mediator); }

    @GetMapping("/search")
    public ResponseEntity<ItunesResponseDto> searchItunes(
            @RequestParam String term, 
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(mediator.send(new SearchItunesQuery(term, limit)));
    }
}
