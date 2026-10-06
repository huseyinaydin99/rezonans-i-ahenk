path = 'music-web/src/main/webapp/index.xhtml'
with open(path, 'r') as f:
    c = f.read()

target = '''    <ui:define name="head">
        <f:metadata>
            <f:viewAction action="#{homeBean.initDashboard}" />
        </f:metadata>
    </ui:define>'''

c = c.replace(target, '''    <f:metadata>
        <f:viewAction action="#{homeBean.initDashboard}" />
    </f:metadata>

    <ui:define name="head">
    </ui:define>''')

with open(path, 'w') as f:
    f.write(c)
