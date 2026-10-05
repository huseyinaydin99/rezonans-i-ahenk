path = 'music-web/src/main/java/tr/com/huseyinaydin/web/beans/RegisterBean.java'
with open(path, 'r') as f:
    c = f.read()

if 'jakarta.faces.context.FacesContext' not in c:
    c = c.replace('import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;', 'import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;\nimport jakarta.faces.context.FacesContext;\nimport java.io.IOException;')

target = '''            this.success = true;
            this.message = "Aramıza hoş geldiniz! Yönlendiriliyorsunuz...";
            System.out.println("[JSF AJAX] Kayıt formu başarıyla işlendi.");'''

replacement = '''            this.success = true;
            this.message = "Aramıza hoş geldiniz! Yönlendiriliyorsunuz...";
            System.out.println("[JSF AJAX] Kayıt formu başarıyla işlendi.");
            
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("login.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }'''

c = c.replace(target, replacement)

with open(path, 'w') as f:
    f.write(c)
