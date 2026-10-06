path = 'music-web/src/main/webapp/resources/css/soundwave.css'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('background: var(--bg-base);', 'background: var(--bg-base) !important;')
c = c.replace('color: var(--text-primary);', 'color: var(--text-primary) !important;')
c = c.replace('background-image:', 'background-color: var(--bg-base) !important;\n    background-image:')

with open(path, 'w') as f:
    f.write(c)
