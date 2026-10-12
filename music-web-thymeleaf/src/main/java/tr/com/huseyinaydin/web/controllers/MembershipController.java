package tr.com.huseyinaydin.web.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.membership.commands.BuyMembershipCommand;
import tr.com.huseyinaydin.application.ports.ITokenService;
import tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException;
import tr.com.huseyinaydin.web.security.AuthCookies;
import tr.com.huseyinaydin.web.security.CurrentUser;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping({"/Membership", "/api/Membership"})
public class MembershipController extends BaseController {

    private final ITokenService tokenService;
    private final AuthCookies authCookies;

    @Autowired
    public MembershipController(Mediator mediator,
                                @Autowired(required = false) ITokenService tokenService,
                                @Autowired(required = false) AuthCookies authCookies) {
        super(mediator);
        this.tokenService = tokenService;
        this.authCookies = authCookies;
    }

    @PostMapping("/Buy")
    public ResponseEntity<Map<String, Object>> buy(@RequestParam("tier") String tier, HttpServletResponse response) {
        var user = CurrentUser.get().orElse(null);
        if (user == null) {
            throw new AuthorizationException("buyMembership", "Lütfen önce giriş yapın.");
        }

        // CQRS Mediator üzerinden paket güncelleme komutunu yürüt
        String canonicalTier = mediator.send(new BuyMembershipCommand(user.email(), tier));

        // Tier token claim'inde taşınır: yeni paket seviyesi ile token üretilip cookie güncellenir
        if (tokenService != null && authCookies != null) {
            String newToken = tokenService.generateAccessToken(user.email(), user.role(), canonicalTier);
            authCookies.write(response, newToken, tokenService.getExpirationSeconds());
        }

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Paketiniz başarıyla " + canonicalTier + " seviyesine yükseltildi!");
        res.put("tier", canonicalTier);
        return ResponseEntity.ok(res);
    }
}
