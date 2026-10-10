package tr.com.huseyinaydin.web.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.ports.ITokenService;
import tr.com.huseyinaydin.web.security.AuthCookies;
import tr.com.huseyinaydin.web.security.CurrentUser;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/Membership", "/api/Membership"})
public class MembershipController {

    private final ITokenService tokenService;
    private final AuthCookies authCookies;

    public MembershipController(ITokenService tokenService, AuthCookies authCookies) {
        this.tokenService = tokenService;
        this.authCookies = authCookies;
    }

    @PostMapping("/Buy")
    public ResponseEntity<Map<String, Object>> buy(@RequestParam("tier") String tier, HttpServletResponse response) {
        var user = CurrentUser.get().orElse(null);
        if (user == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("message", "Lütfen önce giriş yapın.");
            return ResponseEntity.status(401).body(err);
        }

        // Tier token claim'inde taşınır: yeni tier ile token yeniden üretilip cookie yenilenir.
        String newToken = tokenService.generateAccessToken(user.email(), user.role(), tier);
        authCookies.write(response, newToken, tokenService.getExpirationSeconds());

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Paketiniz başarıyla " + tier + " seviyesine yükseltildi!");
        res.put("tier", tier);
        return ResponseEntity.ok(res);
    }
}
