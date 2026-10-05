with open('docker-compose.yml', 'r') as f:
    lines = f.readlines()

new_lines = []
skip = False
for line in lines:
    if line.startswith('  music-app:'):
        skip = True
    elif skip and line.startswith('  ') and not line.startswith('    '):
        skip = False
    
    if not skip:
        new_lines.append(line)

with open('docker-compose.yml', 'w') as f:
    f.writelines(new_lines)
