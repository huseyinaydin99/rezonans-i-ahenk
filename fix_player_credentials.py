path = 'music-web/src/main/webapp/resources/js/player.js'
with open(path, 'r') as f:
    c = f.read()

c = c.replace("fetch('/music-credit-system/api/Song/Play/' + song.id)", "fetch('/music-credit-system/api/Song/Play/' + song.id, { credentials: 'same-origin' })")
c = c.replace("window.location.href = '/Login/SignIn';", "window.location.href = '/music-credit-system/login.xhtml';")

with open(path, 'w') as f:
    f.write(c)
