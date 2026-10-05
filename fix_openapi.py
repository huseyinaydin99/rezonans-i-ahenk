with open('music-web/src/main/java/tr/com/huseyinaydin/web/config/OpenApiConfig.java', 'r') as f:
    content = f.read()

content = content.replace('@Configuration', '@Configuration\n@org.springframework.context.annotation.Profile("openapi")')

with open('music-web/src/main/java/tr/com/huseyinaydin/web/config/OpenApiConfig.java', 'w') as f:
    f.write(content)
