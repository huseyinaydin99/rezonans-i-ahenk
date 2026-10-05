path = 'music-web/src/main/webapp/register.xhtml'
with open(path, 'r') as f:
    c = f.read()

target = '''<h:outputText value="#{registerBean.message}" 
                                          styleClass="alert #{registerBean.success ? 'alert-success' : 'alert-danger'} d-block mb-3 fw-bold" 
                                          rendered="#{not empty registerBean.message}" />'''

replacement = '''<h:outputText value="#{registerBean.message}" 
                                          styleClass="alert #{registerBean.success ? 'alert-success' : 'alert-danger'} d-block mb-3 fw-bold" 
                                          rendered="#{not empty registerBean.message}" />
                            <h:panelGroup rendered="#{registerBean.success}">
                                <script>
                                    setTimeout(function() {
                                        window.location.href = 'login.xhtml';
                                    }, 2000);
                                </script>
                            </h:panelGroup>'''

c = c.replace(target, replacement)

# Fix the login link as well
c = c.replace('<a href="#" class="text-info text-decoration-none fw-bold">Giriş Yap</a>', '<a href="login.xhtml" class="text-info text-decoration-none fw-bold">Giriş Yap</a>')

with open(path, 'w') as f:
    f.write(c)
