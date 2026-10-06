path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

target = '''    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.getElementById('swNavToggle')?.addEventListener('click', function () {
            document.getElementById('swNavLinks')?.classList.toggle('show');
        });
    </script>
    <ui:insert name="scripts" />'''

replacement = '''    <script src="https://code.jquery.com/jquery-3.7.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script src="#{request.contextPath}/resources/js/site.js"></script>
    <script src="#{request.contextPath}/resources/js/notify.js"></script>
    <script src="#{request.contextPath}/resources/js/playlist-add.js"></script>
    <script src="#{request.contextPath}/resources/js/like.js"></script>
    <script src="#{request.contextPath}/resources/js/player.js"></script>
    <script>
        document.getElementById('swNavToggle')?.addEventListener('click', function () {
            document.getElementById('swNavLinks')?.classList.toggle('show');
        });
    </script>
    <ui:insert name="scripts" />'''

c = c.replace(target, replacement)

with open(path, 'w') as f:
    f.write(c)
