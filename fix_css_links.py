path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('<h:outputStylesheet library="css" name="site.css" />', '<link rel="stylesheet" href="#{request.contextPath}/resources/css/site.css" />')
c = c.replace('<h:outputStylesheet library="css" name="soundwave.css" />', '<link rel="stylesheet" href="#{request.contextPath}/resources/css/soundwave.css" />')

with open(path, 'w') as f:
    f.write(c)
