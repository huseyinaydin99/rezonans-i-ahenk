path = 'music-web/src/main/java/tr/com/huseyinaydin/web/controllers/SongController.java'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('@PathVariable Long id', '@PathVariable("id") Long id')

with open(path, 'w') as f:
    f.write(c)
