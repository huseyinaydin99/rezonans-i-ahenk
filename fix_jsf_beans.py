import os
import glob

def fix_file(filepath):
    with open(filepath, 'r') as f:
        content = f.read()

    # We change CDI/JSR-330 annotations to Spring annotations
    content = content.replace('import jakarta.inject.Inject;', 'import org.springframework.beans.factory.annotation.Autowired;')
    content = content.replace('@Inject', '@Autowired')

    content = content.replace('import jakarta.inject.Named;', 'import org.springframework.stereotype.Component;')
    content = content.replace('@Named', '@Component')

    content = content.replace('import jakarta.enterprise.context.RequestScoped;', 'import org.springframework.web.context.annotation.RequestScope;')
    content = content.replace('@RequestScoped', '@RequestScope')
    
    content = content.replace('import jakarta.enterprise.context.SessionScoped;', 'import org.springframework.web.context.annotation.SessionScope;')
    content = content.replace('@SessionScoped', '@SessionScope')
    
    content = content.replace('import jakarta.faces.view.ViewScoped;', 'import org.springframework.web.context.annotation.RequestScope; // Fallback for ViewScope')
    content = content.replace('@ViewScoped', '@RequestScope')

    with open(filepath, 'w') as f:
        f.write(content)

dirs = [
    'music-web/src/main/java/tr/com/huseyinaydin/web/beans/',
    'music-web/src/main/java/tr/com/huseyinaydin/web/jsf/'
]

for d in dirs:
    if os.path.exists(d):
        for f in glob.glob(d + '*.java'):
            fix_file(f)

print("Fixed JSF beans to use Spring annotations")
