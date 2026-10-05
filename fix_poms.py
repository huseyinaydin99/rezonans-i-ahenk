import re

def fix_pom(file_path, dependency_tag):
    with open(file_path, 'r') as f:
        content = f.read()
    
    # We will just remove the SECOND occurrence of the exact dependency
    # A simple hack is to split the content at </dependencies> and remove duplicates inside it
    start_idx = content.find('<dependencies>')
    end_idx = content.find('</dependencies>')
    
    if start_idx == -1 or end_idx == -1: return
    
    deps_block = content[start_idx:end_idx]
    
    # regex to find dependency blocks
    dep_pattern = r'(?s)<dependency>.*?</dependency>'
    deps = re.findall(dep_pattern, deps_block)
    
    seen = set()
    new_deps = []
    
    for dep in deps:
        # extract groupid and artifactid
        group = re.search(r'<groupId>(.*?)</groupId>', dep).group(1)
        artifact = re.search(r'<artifactId>(.*?)</artifactId>', dep).group(1)
        key = f"{group}:{artifact}"
        
        if key not in seen:
            seen.add(key)
            new_deps.append(dep)
            
    # replace block
    new_deps_block = '<dependencies>\n        ' + '\n        '.join(new_deps) + '\n    '
    content = content[:start_idx] + new_deps_block + content[end_idx:]
    
    with open(file_path, 'w') as f:
        f.write(content)

fix_pom('music-infrastructure/pom.xml', 'hibernate-core')
fix_pom('music-persistence/pom.xml', 'h2database')

