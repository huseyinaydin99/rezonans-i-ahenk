import sys

# 1. Remove server side redirect from LoginBean.java
path1 = 'music-web/src/main/java/tr/com/huseyinaydin/web/beans/LoginBean.java'
with open(path1, 'r') as f:
    c1 = f.read()

target1 = '''            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }'''
c1 = c1.replace(target1, '')
with open(path1, 'w') as f:
    f.write(c1)

# 2. Add client side JS redirect to login.xhtml
path2 = 'music-web/src/main/webapp/login.xhtml'
with open(path2, 'r') as f:
    c2 = f.read()

target2 = '''<h:outputText value="#{loginBean.message}" 
                                          styleClass="alert #{loginBean.success ? 'alert-success' : 'alert-danger'} d-block mb-4 fw-bold p-2 text-center" 
                                          rendered="#{not empty loginBean.message}" />'''

replacement2 = '''<h:outputText value="#{loginBean.message}" 
                                          styleClass="alert #{loginBean.success ? 'alert-success' : 'alert-danger'} d-block mb-4 fw-bold p-2 text-center" 
                                          rendered="#{not empty loginBean.message}" />
                            <h:panelGroup rendered="#{loginBean.success}">
                                <script>
                                    setTimeout(function() {
                                        window.location.href = 'index.xhtml';
                                    }, 1500);
                                </script>
                            </h:panelGroup>'''
c2 = c2.replace(target2, replacement2)
with open(path2, 'w') as f:
    f.write(c2)

