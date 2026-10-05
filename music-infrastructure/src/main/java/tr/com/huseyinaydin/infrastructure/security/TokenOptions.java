package tr.com.huseyinaydin.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "banking.security.token-options")
@org.springframework.stereotype.Component
public class TokenOptions {

    private String securityKey = "this-is-a-very-long-and-secure-secret-key-for-jwt-signing";
    private String issuer = "music-app";
    private String audience = "music-app-users";
    private int accessTokenExpiration = 60;
    private int refreshTokenExpiration = 1440;

    public String getSecurityKey() { return securityKey; }
    public void setSecurityKey(String securityKey) { this.securityKey = securityKey; }

    public String getIssuer() { return issuer; }
    public void setIssuer(String issuer) { this.issuer = issuer; }

    public String getAudience() { return audience; }
    public void setAudience(String audience) { this.audience = audience; }

    public int getAccessTokenExpiration() { return accessTokenExpiration; }
    public void setAccessTokenExpiration(int accessTokenExpiration) { this.accessTokenExpiration = accessTokenExpiration; }

    public int getRefreshTokenExpiration() { return refreshTokenExpiration; }
    public void setRefreshTokenExpiration(int refreshTokenExpiration) { this.refreshTokenExpiration = refreshTokenExpiration; }
}
