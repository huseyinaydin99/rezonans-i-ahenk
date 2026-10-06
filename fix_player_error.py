path = 'music-web/src/main/webapp/resources/js/player.js'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('} catch (e) { console.error(e); }', '} catch (e) { console.error(e); notify("Müzik çalınırken bir hata oluştu: " + e, "error"); }')

with open(path, 'w') as f:
    f.write(c)
