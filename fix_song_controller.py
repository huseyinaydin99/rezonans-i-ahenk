path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('@GetMapping("/Song/Play/{id}")', '@GetMapping(value = {"/Song/Play/{id}", "/api/Song/Play/{id}"})')

with open(path, 'w') as f:
    f.write(c)
