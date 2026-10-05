path = 'music-web/src/main/java/tr/com/huseyinaydin/web/config/WebAppInitializer.java'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('tr.com.huseyinaydin.web.config.ActuatorConfig.class', 'tr.com.huseyinaydin.web.config.ActuatorConfig.class,\n                tr.com.huseyinaydin.web.config.MusicJsfConfig.class')

with open(path, 'w') as f:
    f.write(c)
