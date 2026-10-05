import os

pom = 'music-web/pom.xml'
if os.path.exists(pom):
    with open(pom, 'r') as f:
        content = f.read()
    
    content = content.replace('<scope>runtime</scope>', '')
    
    with open(pom, 'w') as f:
        f.write(content)

