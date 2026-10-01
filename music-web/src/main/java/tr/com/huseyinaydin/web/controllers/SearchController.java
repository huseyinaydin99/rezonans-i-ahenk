package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.search.queries.GlobalSearchQuery;
import tr.com.huseyinaydin.application.search.queries.GlobalSearchResponse;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController extends BaseController {

    public SearchController(Mediator mediator) { super(mediator); }

    @GetMapping
    public ResponseEntity<GlobalSearchResponse> search(@RequestParam("q") String keyword) {
        return ResponseEntity.ok(mediator.send(new GlobalSearchQuery(keyword)));
    }
}
