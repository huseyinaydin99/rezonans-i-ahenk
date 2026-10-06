path = 'music-web/src/main/webapp/profile.xhtml'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('<a href="#" class="pr-btn-edit">', '<a href="edit-profile.xhtml" class="pr-btn-edit">')

with open(path, 'w') as f:
    f.write(c)
