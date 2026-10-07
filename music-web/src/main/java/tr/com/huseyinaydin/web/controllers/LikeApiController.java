package tr.com.huseyinaydin.web.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/web-api/Like")
public class LikeApiController {

    // Kullanıcı bazlı (email) beğenilen şarkı ID'lerini bellekte tutuyoruz (Demo)
    private static final Map<String, Set<Long>> userLikes = new HashMap<>();

    @PostMapping("/Toggle/{songId}")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable("songId") Long songId, HttpSession session) {
        String email = (String) session.getAttribute("LOGGED_IN_USER");
        if (email == null) {
            return ResponseEntity.status(401).build();
        }

        userLikes.putIfAbsent(email, new HashSet<>());
        Set<Long> likes = userLikes.get(email);
        
        boolean isLiked;
        if (likes.contains(songId)) {
            likes.remove(songId);
            isLiked = false;
        } else {
            likes.add(songId);
            isLiked = true;
        }

        Map<String, Object> res = new HashMap<>();
        res.put("liked", isLiked);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/MyLikes")
    public ResponseEntity<List<Long>> getMyLikes(HttpSession session) {
        String email = (String) session.getAttribute("LOGGED_IN_USER");
        if (email == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        
        userLikes.putIfAbsent(email, new HashSet<>());
        return ResponseEntity.ok(new ArrayList<>(userLikes.get(email)));
    }
}
