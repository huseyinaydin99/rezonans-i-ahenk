path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

scripts = '''    <script src="https://code.jquery.com/jquery-3.7.0.min.js?v=3"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js?v=3"></script>
    <script src="#{request.contextPath}/resources/js/site.js?v=3"></script>
    <script src="#{request.contextPath}/resources/js/notify.js?v=3"></script>
    <script src="#{request.contextPath}/resources/js/playlist-add.js?v=3"></script>
    <script src="#{request.contextPath}/resources/js/like.js?v=3"></script>
    <script src="#{request.contextPath}/resources/js/player.js?v=3"></script>'''

c = c.replace(scripts, '')

c = c.replace('</h:head>', f'{scripts}\n</h:head>')

with open(path, 'w') as f:
    f.write(c)
