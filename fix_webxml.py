import os

webxml = 'music-web/src/main/webapp/WEB-INF/web.xml'
if os.path.exists(webxml):
    with open(webxml, 'r') as f:
        content = f.read()
    
    welcome = """    <welcome-file-list>
        <welcome-file>index.xhtml</welcome-file>
    </welcome-file-list>
"""
    if '<welcome-file-list>' not in content:
        content = content.replace('</web-app>', welcome + '</web-app>')
        with open(webxml, 'w') as f:
            f.write(content)

