import os

web_pom = 'music-web/pom.xml'

if os.path.exists(web_pom):
    with open(web_pom, 'r') as f:
        w_content = f.read()
    
    cdi_dep = """        <dependency>
            <groupId>org.jboss.weld.servlet</groupId>
            <artifactId>weld-servlet-core</artifactId>
            <version>5.1.2.Final</version>
        </dependency>
"""
    if 'weld-servlet-core' not in w_content:
        w_content = w_content.replace('</dependencies>', cdi_dep + '    </dependencies>')
        with open(web_pom, 'w') as f:
            f.write(w_content)

