with open('Dockerfile', 'r') as f:
    content = f.read()

# Change it back to -DskipTests so test-jar plugins still run but tests aren't executed
content = content.replace('mvn clean package -Dmaven.test.skip=true', 'mvn clean package -DskipTests')

with open('Dockerfile', 'w') as f:
    f.write(content)
