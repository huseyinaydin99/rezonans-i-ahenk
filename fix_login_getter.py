path = 'music-web/src/main/java/tr/com/huseyinaydin/web/beans/LoginBean.java'
with open(path, 'r') as f:
    c = f.read()

target = '''public boolean isSuccess() { 
        boolean current = success; 
        success = false; // Auto reset 
        return current; 
    }'''

replacement = '''public boolean isSuccess() { 
        return success; 
    }'''

c = c.replace(target, replacement)
with open(path, 'w') as f:
    f.write(c)
