package tr.com.huseyinaydin.application.admin.dtos;

public class UserRoleDto {
    private String userId;
    private String username;
    private String email;
    private String currentRole; // Örn: ROLE_USER, ROLE_ARTIST, ROLE_ADMIN
    private boolean isBanned;

    public UserRoleDto(String userId, String username, String email, String currentRole, boolean isBanned) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.currentRole = currentRole;
        this.isBanned = isBanned;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getCurrentRole() { return currentRole; }
    public void setCurrentRole(String currentRole) { this.currentRole = currentRole; }
    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean isBanned) { this.isBanned = isBanned; }
}
