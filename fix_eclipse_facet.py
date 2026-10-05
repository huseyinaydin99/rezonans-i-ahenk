import os

facet_path = 'music-web/.settings/org.eclipse.wst.common.project.facet.core.xml'
if os.path.exists(facet_path):
    with open(facet_path, 'r') as f:
        content = f.read()
    
    # Downgrade jst.web to 5.0 to be compatible with Tomcat 10.0
    content = content.replace('<installed facet="jst.web" version="6.0"/>', '<installed facet="jst.web" version="5.0"/>')
    
    with open(facet_path, 'w') as f:
        f.write(content)

