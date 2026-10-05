with open('Dockerfile', 'r') as f:
    content = f.read()

content = content.replace('eclipse-temurin:17-jre-alpine', 'eclipse-temurin:17-jre')

with open('Dockerfile', 'w') as f:
    f.write(content)
