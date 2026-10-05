with open('Dockerfile', 'r') as f:
    content = f.read()

content = content.replace('mvn clean package -DskipTests', 'mvn clean package -Dmaven.test.skip=true')

with open('Dockerfile', 'w') as f:
    f.write(content)
