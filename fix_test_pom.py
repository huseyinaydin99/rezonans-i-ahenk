import os

pom = 'music-web/pom.xml'
if os.path.exists(pom):
    with open(pom, 'r') as f:
        content = f.read()
    
    # Add spring-boot-starter-test just in case
    dep = """
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>"""
    content = content.replace('</dependencies>', dep)
    with open(pom, 'w') as f:
        f.write(content)

