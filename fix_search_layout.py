path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

target = '''                <div class="sw-search">
                    <input type="search" name="q" placeholder="Şarkı, sanatçı, tür ara..." />
                    <button type="button">Ara</button>
                </div>'''

replacement = '''                <form class="sw-search" action="search.xhtml" method="get">
                    <input type="search" name="q" placeholder="Şarkı, sanatçı, tür ara..." />
                    <button type="submit">Ara</button>
                </form>'''

c = c.replace(target, replacement)

with open(path, 'w') as f:
    f.write(c)
