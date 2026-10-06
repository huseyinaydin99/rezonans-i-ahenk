import os

# 1. artists.xhtml
path = 'music-web/src/main/webapp/artists.xhtml'
if os.path.exists(path):
    with open(path, 'r') as f:
        c = f.read()
    c = c.replace('href="#"', 'href="artist-details.xhtml?id=1"')
    with open(path, 'w') as f:
        f.write(c)

# 2. genres.xhtml
path = 'music-web/src/main/webapp/genres.xhtml'
if os.path.exists(path):
    with open(path, 'r') as f:
        c = f.read()
    c = c.replace('href="#"', 'href="genre-details.xhtml?id=1"')
    with open(path, 'w') as f:
        f.write(c)

# 3. playlists.xhtml
path = 'music-web/src/main/webapp/playlists.xhtml'
if os.path.exists(path):
    with open(path, 'r') as f:
        c = f.read()
    c = c.replace('href="#"', 'href="playlist-details.xhtml?id=1"')
    with open(path, 'w') as f:
        f.write(c)

# 4. login.xhtml
path = 'music-web/src/main/webapp/login.xhtml'
if os.path.exists(path):
    with open(path, 'r') as f:
        c = f.read()
    c = c.replace('href="#"', 'href="forgot-password.xhtml"')
    with open(path, 'w') as f:
        f.write(c)
