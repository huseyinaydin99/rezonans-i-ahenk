import re
with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/TokenOptions.java', 'r') as f:
    content = f.read()

content = content.replace('private String securityKey;', 'private String securityKey = "this-is-a-very-long-and-secure-secret-key-for-jwt-signing";')
content = content.replace('private String issuer;', 'private String issuer = "music-app";')
content = content.replace('private String audience;', 'private String audience = "music-app-users";')
content = content.replace('private int accessTokenExpiration;', 'private int accessTokenExpiration = 60;')
content = content.replace('private int refreshTokenExpiration;', 'private int refreshTokenExpiration = 1440;')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/TokenOptions.java', 'w') as f:
    f.write(content)
