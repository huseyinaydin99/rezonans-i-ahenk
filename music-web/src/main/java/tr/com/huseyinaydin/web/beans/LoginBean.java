package tr.com.huseyinaydin.web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.ILoginService;
import tr.com.huseyinaydin.application.auth.dtos.LoginRequestDto;
import tr.com.huseyinaydin.application.auth.dtos.ResponseTokenDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import java.io.Serializable;
import java.util.Map;
import java.io.IOException;

@Named
@SessionScoped
public class LoginBean implements Serializable {

    @Inject
    private ILoginService loginService;

    private String email;
    private String password;
    
    private String message;
    private boolean success;

    // Oturum verileri
    private String loggedInUserRole;
    private boolean isLoggedIn = false;

    public void doLogin() {
        try {
            LoginRequestDto req = new LoginRequestDto();
            req.setEmail(email);
            req.setPassword(password);

            ResponseTokenDto tokenResponse = loginService.authenticate(req);
            
            // JWT Token'ı güvenli JSF Session'ına kaydet (Authorization için)
            FacesContext context = FacesContext.getCurrentInstance();
            Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
            sessionMap.put("JWT_TOKEN", tokenResponse.getAccessToken());
            sessionMap.put("USER_ROLE", tokenResponse.getUserRole());
            
            this.isLoggedIn = true;
            this.loggedInUserRole = tokenResponse.getUserRole();
            
            this.success = true;
            this.message = "Giriş başarılı! Ana sayfaya yönlendiriliyorsunuz...";
            System.out.println("[JSF AJAX] Token JSF Session havuzuna (Cookie) güvenle şifrelendi.");
            
        } catch (BusinessException e) {
            this.success = false;
            this.message = e.getMessage();
        } catch (Exception e) {
            this.success = false;
            this.message = "Sunucu bağlantı hatası.";
        }
    }

    /**
     * Güvenli Çıkış (Logout) Akışı
     * Kullanıcının JSF oturumundaki JWT token'ını ve rollerini temizler, 
     * ardından Login ekranına yönlendirir.
     */
    public void logout() throws IOException {
        System.out.println("[GİRİŞ SİSTEMİ] Kullanıcı oturumu sonlandırma talebi (Logout) alındı.");
        
        FacesContext context = FacesContext.getCurrentInstance();
        
        // Session haritasından gizli bilgileri temizle
        Map<String, Object> sessionMap = context.getExternalContext().getSessionMap();
        sessionMap.remove("JWT_TOKEN");
        sessionMap.remove("USER_ROLE");
        
        // Bean'in dahili durumunu sıfırla
        this.isLoggedIn = false;
        this.loggedInUserRole = null;
        this.email = null;
        this.password = null;
        this.message = null;
        
        // Güvenlik: Tüm JSF HTTP oturumunu kökten geçersiz kıl (Invalidate)
        context.getExternalContext().invalidateSession();
        
        System.out.println("-> Oturum (Session) temizlendi. Token silindi.");
        
        // Çıkış yapıldıktan sonra Login sayfasına yönlendir
        context.getExternalContext().redirect(context.getExternalContext().getRequestContextPath() + "/login.xhtml");
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
    public boolean getIsLoggedIn() { return isLoggedIn; }
    public String getLoggedInUserRole() { return loggedInUserRole; }
}
