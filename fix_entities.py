path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('&copy;', '&#169;')

with open(path, 'w') as f:
    f.write(c)
