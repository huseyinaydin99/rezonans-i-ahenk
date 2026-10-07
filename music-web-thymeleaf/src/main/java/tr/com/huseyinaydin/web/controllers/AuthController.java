package tr.com.huseyinaydin.web.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import tr.com.huseyinaydin.application.ports.ILoginService;
import tr.com.huseyinaydin.application.auth.dtos.LoginRequestDto;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import java.util.Collections;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private ILoginService loginService;

    @Autowired
    private tr.com.huseyinaydin.application.ports.IRegisterService registerService;

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("email") String email, 
                          @RequestParam("password") String password, 
                          HttpSession session, Model model, jakarta.servlet.http.HttpServletResponse response) {
        try {
            LoginRequestDto req = new LoginRequestDto();
            req.setEmail(email);
            req.setPassword(password);

            ResponseTokenDto tokenResponse = loginService.authenticate(req);
            
            session.setAttribute("JWT_TOKEN", tokenResponse.getAccessToken());
            session.setAttribute("USER_ROLE", tokenResponse.getUserRole());
            session.setAttribute("LOGGED_IN_USER", email);
            session.setAttribute("USER_TIER", "Premium");

            List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(tokenResponse.getUserRole()));
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(auth);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
            
            return "redirect:/";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Sunucu hatası: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/doRegister")
    public String doRegister(@RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("password") String password,
                             @RequestParam(value = "artistAccount", required = false) String artistAccount,
                             Model model) {
        try {
            tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto req = new tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto();
            req.setUsername(username);
            req.setEmail(email);
            req.setPassword(password);
            req.setFullName(username); // formda fullname yok, username'i atıyoruz
            req.setArtistAccount("on".equals(artistAccount));
            
            registerService.registerNewUser(req);
            model.addAttribute("message", "Kayıt başarılı! Lütfen giriş yapınız.");
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "Kayıt sırasında bir hata oluştu: " + e.getMessage());
        }
        return "register";
    }

    @PostMapping("/doLogout")
    public String doLogout(HttpSession session, jakarta.servlet.http.HttpServletResponse response) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
