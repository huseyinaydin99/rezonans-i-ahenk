package tr.com.huseyinaydin.application.auth.dtos;

public class RegisterRequestDto {
    private String username;
    private String email;
    private String password;
    private String fullName;
    private boolean isArtistAccount; // Sanatçı hesabı mı oluşturuluyor?

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public boolean isArtistAccount() { return isArtistAccount; }
    public void setArtistAccount(boolean isArtistAccount) { this.isArtistAccount = isArtistAccount; }
}
