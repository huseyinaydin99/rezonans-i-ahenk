with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/TokenOptions.java', 'r') as f:
    content = f.read()

content = content.replace('public class TokenOptions {', '@org.springframework.stereotype.Component\npublic class TokenOptions {')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/TokenOptions.java', 'w') as f:
    f.write(content)
