package tr.com.huseyinaydin.domain.user;

import tr.com.huseyinaydin.domain.common.Entity;
import tr.com.huseyinaydin.domain.enums.UserRole;

import java.util.UUID;
import java.util.regex.Pattern;

public class ApplicationUser extends Entity<UUID> {

    private UUID customerId;
    private String email;
    private byte[] passwordHash;
    private byte[] passwordSalt;
    private boolean isActive;
    private UserRole role;
    
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    protected ApplicationUser() {
        super();
    }

    public ApplicationUser(UUID customerId, String email,
                           byte[] passwordHash, byte[] passwordSalt, UserRole role) {
        super();
        
        UUID effectiveCustomerId = customerId != null ? customerId : UUID.randomUUID();
        if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("❌ [DOMAIN KURAL İHLALİ]: Geçerli bir e-posta adresi girilmelidir: " + email);
        }
        if (passwordHash == null || passwordHash.length == 0 || passwordSalt == null || passwordSalt.length == 0) {
            throw new IllegalArgumentException("❌ [DOMAIN KURAL İHLALİ]: Şifre ve güvenlik tuzu (Salt) değerleri boş bırakılamaz.");
        }
        if (role == null) {
            throw new IllegalArgumentException("❌ [DOMAIN KURAL İHLALİ]: Kullanıcıya ait rol bilgisi eksik.");
        }

        this.id = UUID.randomUUID();
        this.customerId = effectiveCustomerId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
        this.role = role;
        this.isActive = true;

        registerEvent(new tr.com.huseyinaydin.domain.events.UserRegisteredEvent(this.id, this.customerId, this.email, this.role));
    }

    public void changeRole(UserRole role) {
        if (role == null) throw new IllegalArgumentException("❌ [DOMAIN KURAL İHLALİ]: Yeni rol bilgisi boş olamaz.");
        this.role = role;
    }

    public void updatePassword(byte[] passwordHash, byte[] passwordSalt) {
        if (passwordHash == null || passwordHash.length == 0 || passwordSalt == null || passwordSalt.length == 0) {
            throw new IllegalArgumentException("❌ [DOMAIN KURAL İHLALİ]: Yeni şifre ve güvenlik tuzu (Salt) değerleri boş bırakılamaz.");
        }
        this.passwordHash = passwordHash;
        this.passwordSalt = passwordSalt;
    }

    public void updateEmail(String email) {
        if (email == null || email.trim().isEmpty() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("❌ [DOMAIN KURAL İHLALİ]: Geçerli bir yeni e-posta adresi girilmelidir.");
        }
        this.email = email;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public UUID getCustomerId() { return customerId; }
    public String getEmail() { return email; }
    public byte[] getPasswordHash() { return passwordHash; }
    public byte[] getPasswordSalt() { return passwordSalt; }
    public boolean isActive() { return isActive; }
    public UserRole getRole() { return role; }
}
