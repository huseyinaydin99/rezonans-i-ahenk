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

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("email") String email, 
                          @RequestParam("password") String password, 
                          HttpSession session, Model model) {
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
            
            return "redirect:/index.xhtml";
        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Sunucu hatası: " + e.getMessage());
            return "login";
        }
    }

    @PostMapping("/doLogout")
    public String doLogout(HttpSession session) {
        session.invalidate();
        SecurityContextHolder.clearContext();
        return "redirect:/login.xhtml";
    }
}
