import os

parent_pom = 'pom.xml'
web_pom = 'music-web/pom.xml'

if os.path.exists(parent_pom):
    with open(parent_pom, 'r') as f:
        p_content = f.read()
    
    impl_dep = """
            <dependency>
                <groupId>org.glassfish</groupId>
                <artifactId>jakarta.faces</artifactId>
                <version>${jakarta.faces.version}</version>
            </dependency>
"""
    if 'org.glassfish' not in p_content:
        p_content = p_content.replace('</dependencies>', impl_dep + '        </dependencies>')
        with open(parent_pom, 'w') as f:
            f.write(p_content)

if os.path.exists(web_pom):
    with open(web_pom, 'r') as f:
        w_content = f.read()
    
    old_dep = """        <dependency>
            <groupId>jakarta.faces</groupId>
            <artifactId>jakarta.faces-api</artifactId>
            <scope>provided</scope>
        </dependency>"""
    
    new_dep = """        <dependency>
            <groupId>org.glassfish</groupId>
            <artifactId>jakarta.faces</artifactId>
        </dependency>"""
    
    if 'jakarta.faces-api' in w_content:
        w_content = w_content.replace(old_dep, new_dep)
        with open(web_pom, 'w') as f:
            f.write(w_content)

