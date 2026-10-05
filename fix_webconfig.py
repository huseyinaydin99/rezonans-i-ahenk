import os

wc = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/MusicWebConfig.java'
if os.path.exists(wc):
    with open(wc, 'r') as f:
        content = f.read()
    
    # Exclude SecurityConfig
    import_exclude = 'import org.springframework.context.annotation.FilterType;\n'
    if import_exclude not in content:
        content = content.replace('import org.springframework.context.annotation.ComponentScan;', import_exclude + 'import org.springframework.context.annotation.ComponentScan;')
    
    content = content.replace('@ComponentScan("tr.com.huseyinaydin.web")', '@ComponentScan(basePackages = "tr.com.huseyinaydin.web", excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class))')
    
    with open(wc, 'w') as f:
        f.write(content)

