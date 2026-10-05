path = 'music-web/src/main/java/tr/com/huseyinaydin/web/beans/LoginBean.java'
with open(path, 'r') as f:
    c = f.read()

target = '''            this.success = true;
            this.message = "Giriş başarılı! Ana sayfaya yönlendiriliyorsunuz...";
            System.out.println("[JSF AJAX] Token JSF Session havuzuna (Cookie) güvenle şifrelendi.");'''

replacement = '''            this.success = true;
            this.message = "Giriş başarılı! Ana sayfaya yönlendiriliyorsunuz...";
            System.out.println("[JSF AJAX] Token JSF Session havuzuna (Cookie) güvenle şifrelendi.");
            
            try {
                FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
            } catch (IOException e) {
                e.printStackTrace();
            }'''

c = c.replace(target, replacement)

with open(path, 'w') as f:
    f.write(c)
