import os

faces = 'music-web/src/main/webapp/WEB-INF/faces-config.xml'
if os.path.exists(faces):
    with open(faces, 'r') as f:
        content = f.read()
    
    el = '        <el-resolver>org.springframework.web.jsf.el.SpringBeanFacesELResolver</el-resolver>\n'
    if '<el-resolver>' not in content:
        content = content.replace('<application>', '<application>\n' + el)
        with open(faces, 'w') as f:
            f.write(content)

