package tr.com.huseyinaydin.web.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import tr.com.huseyinaydin.application.ports.TokenClaims;

/** Şablonlara token'dan türetilen kullanıcıyı ve flash mesajlarını sağlar (session yerine). */
@ControllerAdvice(annotations = org.springframework.stereotype.Controller.class)
public class GlobalModelAttributes {

    @ModelAttribute
    public void populate(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("currentUser", CurrentUser.get().map(TokenClaims::email).orElse(null));
        String accept = request.getHeader("Accept");
        if (accept == null || !accept.contains("text/html")) return; // fetch/AJAX istekleri flash mesajını tüketmesin
        String error = FlashCookies.consume(request, response, "error");
        if (error != null) model.addAttribute("error", error);
        String message = FlashCookies.consume(request, response, "message");
        if (message != null) model.addAttribute("message", message);
    }
}
