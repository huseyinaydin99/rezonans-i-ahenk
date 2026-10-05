import os

init_path = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/WebAppInitializer.java'
if os.path.exists(init_path):
    with open(init_path, 'r') as f:
        content = f.read()
    
    # We will just fix the syntax error
    content = content.replace('); //\n                \n                \n                \n        );', ');')
    
    with open(init_path, 'w') as f:
        f.write(content)

