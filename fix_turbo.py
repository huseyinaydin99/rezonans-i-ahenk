import os

# 1. layout.xhtml
path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

# Add turbo
if 'hotwired/turbo' not in c:
    c = c.replace('<h:head>', '<h:head>\n    <script type="module">import hotwiredTurbo from "https://cdn.skypack.dev/@hotwired/turbo";</script>')

# Disable turbo on nav form (logout)
c = c.replace('<h:form id="navForm" prependId="false">', '<h:form id="navForm" prependId="false" pt:data-turbo="false" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough">')

# Make player permanent
c = c.replace('<div id="globalPlayer" class="gp-bar" style="display:none;">', '<div id="globalPlayer" class="gp-bar" style="display:none;" data-turbo-permanent="true">')
# Make playlist modal permanent just in case
c = c.replace('<div class="pladd-overlay" id="pladdOverlay">', '<div class="pladd-overlay" id="pladdOverlay" data-turbo-permanent="true">')

with open(path, 'w') as f:
    f.write(c)

# 2. Add pt:data-turbo="false" to login/register
for p in ['login.xhtml', 'register.xhtml']:
    fp = f'music-web/src/main/webapp/{p}'
    if os.path.exists(fp):
        with open(fp, 'r') as f:
            c = f.read()
        if 'xmlns:pt=' not in c:
            c = c.replace('<h:form', '<h:form pt:data-turbo="false" xmlns:pt="http://xmlns.jcp.org/jsf/passthrough"')
        else:
            c = c.replace('<h:form', '<h:form pt:data-turbo="false"')
        with open(fp, 'w') as f:
            f.write(c)

# 3. Update JS files to listen for turbo:load
js_files = ['player.js', 'like.js', 'playlist-add.js']
for p in js_files:
    fp = f'music-web/src/main/webapp/resources/js/{p}'
    with open(fp, 'r') as f:
        c = f.read()
    c = c.replace("window.addEventListener('DOMContentLoaded',", "document.addEventListener('turbo:load',")
    # Also support DOMContentLoaded just in case
    c += "\nwindow.addEventListener('DOMContentLoaded', () => document.dispatchEvent(new Event('turbo:load')));\n"
    with open(fp, 'w') as f:
        f.write(c)

