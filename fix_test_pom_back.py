import os

pom = 'music-web/pom.xml'
if os.path.exists(pom):
    with open(pom, 'r') as f:
        content = f.read()
    
    # Remove the duplicated block I added
    dep = """
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>"""
    content = content.replace(dep, '</dependencies>')
    with open(pom, 'w') as f:
        f.write(content)

