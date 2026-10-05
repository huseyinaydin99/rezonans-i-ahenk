with open('music-domain/src/main/java/tr/com/huseyinaydin/domain/repositories/IAsyncRepository.java', 'r') as f:
    content = f.read()

content = content.replace('public interface IAsyncRepository<TEntity extends BaseEntity<TId>, TId>', 'public interface IAsyncRepository<TEntity, TId>')

with open('music-domain/src/main/java/tr/com/huseyinaydin/domain/repositories/IAsyncRepository.java', 'w') as f:
    f.write(content)
