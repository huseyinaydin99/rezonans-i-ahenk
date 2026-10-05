path = './music-persistence/src/main/java/tr/com/huseyinaydin/persistence/repositories/ApplicationUserJpaRepository.java'
with open(path, 'r') as f:
    c = f.read()
c = c.replace('getResultStream().findFirst()', 'getResultList().stream().findFirst()')
with open(path, 'w') as f:
    f.write(c)

path2 = './music-application/src/main/java/tr/com/huseyinaydin/application/auth/services/LoginServiceImpl.java'
with open(path2, 'r') as f:
    c2 = f.read()
if 'import org.springframework.transaction.annotation.Transactional;' not in c2:
    c2 = c2.replace('import org.springframework.stereotype.Service;', 'import org.springframework.stereotype.Service;\nimport org.springframework.transaction.annotation.Transactional;')

c2 = c2.replace('public ResponseTokenDto authenticate(LoginRequestDto request)', '@Transactional(readOnly = true)\n    public ResponseTokenDto authenticate(LoginRequestDto request)')
with open(path2, 'w') as f:
    f.write(c2)
