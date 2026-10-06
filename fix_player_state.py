path = 'music-web/src/main/webapp/resources/js/player.js'
with open(path, 'r') as f:
    c = f.read()

replacement = '''
if (!window.player) {
    window.player = {
'''

c = c.replace('const player = {', replacement)
c = c.replace('window.addEventListener(\'DOMContentLoaded\', () => document.dispatchEvent(new Event(\'turbo:load\')));', 'window.addEventListener(\'DOMContentLoaded\', () => document.dispatchEvent(new Event(\'turbo:load\')));\n}')

with open(path, 'w') as f:
    f.write(c)
