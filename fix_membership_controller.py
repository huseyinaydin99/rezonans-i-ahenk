path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/MembershipController.java'
c = '''package tr.com.huseyinaydin.web.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/Membership", "/api/Membership"})
public class MembershipController {

    @PostMapping("/Buy")
    public ResponseEntity<Map<String, Object>> buy(@RequestParam("tier") String tier, HttpSession session) {
        Object user = session.getAttribute("LOGGED_IN_USER");
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Lütfen önce giriş yapın.");
            return ResponseEntity.status(401).body(err);
        }

        // Tier'ı kaydet
        session.setAttribute("USER_TIER", tier);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Paketiniz başarıyla " + tier + " seviyesine yükseltildi!");
        res.put("tier", tier);
        res.put("token", "dummy-jwt-for-client");
        return ResponseEntity.ok(res);
    }
}
'''
with open(path, 'w') as f:
    f.write(c)
