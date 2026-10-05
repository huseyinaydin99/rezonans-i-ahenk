import re
with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/TokenOptions.java', 'r') as f:
    content = f.read()

content = content.replace('@Component\n', '')
content = content.replace('import org.springframework.stereotype.Component;\n', '')

with open('music-infrastructure/src/main/java/tr/com/huseyinaydin/infrastructure/security/TokenOptions.java', 'w') as f:
    f.write(content)
