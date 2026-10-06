path = 'music-web/src/main/webapp/resources/js/player.js'
with open(path, 'r') as f:
    c = f.read()

c = c.replace("fetch('/Song/Play/' + song.id)", "fetch('/music-credit-system/api/Song/Play/' + song.id)")

with open(path, 'w') as f:
    f.write(c)

path2 = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path2, 'r') as f:
    c2 = f.read()

c2 = c2.replace('@GetMapping("/Song/Play/{id}")', '@GetMapping("/Song/Play/{id}")') # Wait, if Dispatcher is /api/*, then class needs @RequestMapping("/Song")?
# Actually if DispatcherServlet maps to /api/*, the request path inside controller is stripped of /api
# So @GetMapping("/Song/Play/{id}") will match /api/Song/Play/{id}
