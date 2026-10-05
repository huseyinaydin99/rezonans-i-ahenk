path = 'music-web/src/main/java/tr/com/huseyinaydin/web/beans/LoginBean.java'
with open(path, 'r') as f:
    c = f.read()

target = '''        } catch (Exception e) {
            this.success = false;
            this.message = "Sunucu bağlantı hatası.";
        }'''

replacement = '''        } catch (Exception e) {
            e.printStackTrace();
            this.success = false;
            this.message = "Sunucu hatası: " + e.toString();
        }'''

c = c.replace(target, replacement)
with open(path, 'w') as f:
    f.write(c)
