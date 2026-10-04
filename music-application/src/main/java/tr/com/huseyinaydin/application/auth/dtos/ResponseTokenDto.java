package tr.com.huseyinaydin.application.auth.dtos;

public class ResponseTokenDto {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String userRole;

    public ResponseTokenDto(String accessToken, String refreshToken, long expiresIn, String userRole) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.userRole = userRole;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public long getExpiresIn() { return expiresIn; }
    public String getUserRole() { return userRole; }
}
