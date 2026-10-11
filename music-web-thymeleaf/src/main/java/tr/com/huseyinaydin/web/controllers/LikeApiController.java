package tr.com.huseyinaydin.web.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.web.security.CurrentUser;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.like.commands.ToggleLikeCommand;
import tr.com.huseyinaydin.application.like.queries.GetLikedSongsQuery;

@RestController
@RequestMapping("/web-api/Like")
public class LikeApiController {

    @Autowired
    private Mediator mediator;

    @PostMapping("/Toggle/{songId}")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("songId") Long songId) {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            throw new tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException("toggleLike", "Beğenmek için giriş yapmalısınız.");
        }

        Map<String, Object> result = mediator.send(new ToggleLikeCommand(email, songId));
        return ResponseEntity.ok(result);
    }

    @GetMapping("/MyLikes")
    public ResponseEntity<List<Long>> getMyLikes() {
        String email = CurrentUser.get().map(c -> c.email()).orElse(null);
        if (email == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        List<Long> result = mediator.query(new GetLikedSongsQuery(email));
        return ResponseEntity.ok(result);
    }
}
