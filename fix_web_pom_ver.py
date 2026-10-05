import os

web_pom = 'music-web/pom.xml'

if os.path.exists(web_pom):
    with open(web_pom, 'r') as f:
        w_content = f.read()
    
    old_dep = """        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>jakarta.faces</artifactId>
        </dependency>"""
    
    new_dep = """        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>jakarta.faces</artifactId>
            <version>4.0.1</version>
        </dependency>"""
    
    if 'org.glassfish' in w_content:
        w_content = w_content.replace(old_dep, new_dep)
        with open(web_pom, 'w') as f:
            f.write(w_content)

