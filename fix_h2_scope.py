import re
with open('pom.xml', 'r') as f:
    content = f.read()

# Find the h2 dependency block and remove the test scope
# <groupId>com.h2database</groupId>\s*<artifactId>h2</artifactId>\s*<version>\$\{h2.version\}</version>\s*<scope>test</scope>

content = re.sub(r'(<groupId>com.h2database</groupId>\s*<artifactId>h2</artifactId>\s*<version>\$\{h2.version\}</version>\s*)<scope>test</scope>', r'\1<scope>runtime</scope>', content)

with open('pom.xml', 'w') as f:
    f.write(content)

