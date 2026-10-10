package tr.com.huseyinaydin.web.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tr.com.huseyinaydin.application.auth.commands.LoginCommand;
import tr.com.huseyinaydin.application.auth.commands.RegisterCommand;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.web.security.AuthCookies;
import tr.com.huseyinaydin.web.security.FlashCookies;

/** Stateless kimlik doğrulama: JWT, HttpOnly cookie ile taşınır; sunucuda oturum tutulmaz. */
@Controller
public class AuthController {

    @Autowired
    private Mediator mediator;

    @Autowired
    private AuthCookies authCookies;

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("email") String email,
                          @RequestParam("password") String password,
                          HttpServletResponse response) {
        try {
            ResponseTokenDto token = mediator.send(new LoginCommand(email, password));
            authCookies.write(response, token.getAccessToken(), token.getExpiresIn());
            return "redirect:/";
        } catch (BusinessException e) {
            FlashCookies.set(response, "error", e.getMessage());
            return "redirect:/login";
        } catch (Exception e) {
            FlashCookies.set(response, "error", "Sunucu hatası: " + e.getMessage());
            return "redirect:/login";
        }
    }

    @PostMapping("/doRegister")
    public String doRegister(@RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam(value = "artistAccount", required = false) String artistAccount,
                             HttpServletResponse response) {
        try {
            boolean isArtist = "on".equals(artistAccount);
            mediator.send(new RegisterCommand(username, email, password, isArtist));
            FlashCookies.set(response, "message", "Kayıt başarılı! Lütfen giriş yapınız.");
            return "redirect:/login";
        } catch (BusinessException e) {
            FlashCookies.set(response, "error", e.getMessage());
            return "redirect:/register";
        } catch (Exception e) {
            FlashCookies.set(response, "error", "Kayıt sırasında bir hata oluştu: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @PostMapping("/doLogout")
    public String doLogout(HttpServletResponse response) {
        authCookies.clear(response);
        return "redirect:/login";
    }
}
