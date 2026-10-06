path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('.js"></script>', '.js?v=3"></script>')
c = c.replace('.css" />', '.css?v=3" />')

with open(path, 'w') as f:
    f.write(c)
