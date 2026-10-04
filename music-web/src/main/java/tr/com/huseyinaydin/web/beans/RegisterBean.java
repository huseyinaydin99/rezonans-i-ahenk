package tr.com.huseyinaydin.web.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.IRegisterService;
import tr.com.huseyinaydin.application.auth.dtos.RegisterRequestDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

@Named
@RequestScoped
public class RegisterBean {

    @Inject
    private IRegisterService registerService;

    private String username;
    private String email;
    private String password;
    private String fullName;
    private boolean artistAccount;
    
    private String message;
    private boolean success;

    public void doRegister() {
        try {
            RegisterRequestDto req = new RegisterRequestDto();
            req.setUsername(username);
            req.setEmail(email);
            req.setPassword(password);
            req.setFullName(fullName);
            req.setArtistAccount(artistAccount);

            registerService.registerNewUser(req);
            
            this.success = true;
            this.message = "Aramıza hoş geldiniz! Yönlendiriliyorsunuz...";
            System.out.println("[JSF AJAX] Kayıt formu başarıyla işlendi.");
            
        } catch (BusinessException e) {
            this.success = false;
            this.message = e.getMessage();
        } catch (Exception e) {
            this.success = false;
            this.message = "Beklenmeyen bir sistem hatası oluştu.";
        }
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public boolean isArtistAccount() { return artistAccount; }
    public void setArtistAccount(boolean artistAccount) { this.artistAccount = artistAccount; }
    public String getMessage() { return message; }
    public boolean isSuccess() { return success; }
}
