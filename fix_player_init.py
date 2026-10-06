path = 'music-web/src/main/webapp/resources/js/player.js'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('init() {', 'init() {\n        if (this._initialized) return;\n        this._initialized = true;')

with open(path, 'w') as f:
    f.write(c)
