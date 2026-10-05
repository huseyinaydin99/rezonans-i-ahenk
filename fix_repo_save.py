path = './music-application/src/main/java/tr/com/huseyinaydin/application/auth/services/RegisterServiceImpl.java'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('userRepository.add(user);', 'userRepository.save(user);')

with open(path, 'w') as f:
    f.write(c)
