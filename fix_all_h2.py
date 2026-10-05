import os
import re

for root, dirs, files in os.walk('.'):
    for file in files:
        if file == 'pom.xml':
            filepath = os.path.join(root, file)
            with open(filepath, 'r') as f:
                content = f.read()
            
            # Find explicitly declared h2 test scopes in children
            content = re.sub(r'(<groupId>com.h2database</groupId>\s*<artifactId>h2</artifactId>\s*)<scope>test</scope>', r'\1', content)
            
            # Also remove test scope from the root pom just in case
            content = re.sub(r'(<groupId>com.h2database</groupId>\s*<artifactId>h2</artifactId>\s*<version>\$\{h2.version\}</version>\s*)<scope>test</scope>', r'\1', content)
            
            with open(filepath, 'w') as f:
                f.write(content)
