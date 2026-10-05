import os

# 1. Fix MusicDataSeeder
seeder = 'music-persistence/src/main/java/tr/com/huseyinaydin/persistence/seed/MusicDataSeeder.java'
if os.path.exists(seeder):
    with open(seeder, 'w') as f:
        f.write("""package tr.com.huseyinaydin.persistence.seed;
import org.springframework.stereotype.Component;
@Component
public class MusicDataSeeder {
    public void seedData() { }
}
""")

# 2. Fix JpaRepositoryBase
jpa = 'music-persistence/src/main/java/tr/com/huseyinaydin/persistence/repositories/JpaRepositoryBase.java'
if os.path.exists(jpa):
    with open(jpa, 'r') as f:
        content = f.read()
    content = content.replace('class JpaRepositoryBase<TEntity, TId>', 'class JpaRepositoryBase<TEntity extends tr.com.huseyinaydin.domain.common.BaseEntity<TId>, TId>')
    with open(jpa, 'w') as f:
        f.write(content)

# 3. Add BaseEntity<UUID> to Domain entities
entities = ['Artist.java', 'Genre.java', 'Song.java', 'Playlist.java']
for e in entities:
    path = f'music-domain/src/main/java/tr/com/huseyinaydin/domain/{e.lower().split(".")[0]}/{e}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        content = content.replace(f'public class {e[:-5]} {{', f'public class {e[:-5]} extends tr.com.huseyinaydin.domain.common.BaseEntity<java.util.UUID> {{')
        content = content.replace(f'public class {e[:-5]} \n', f'public class {e[:-5]} extends tr.com.huseyinaydin.domain.common.BaseEntity<java.util.UUID> \n')
        with open(path, 'w') as f:
            f.write(content)

# 4. Fix IAsyncRepository
iasync = 'music-domain/src/main/java/tr/com/huseyinaydin/domain/repositories/IAsyncRepository.java'
if os.path.exists(iasync):
    with open(iasync, 'r') as f:
        content = f.read()
    content = content.replace('IAsyncRepository<TEntity, TId>', 'IAsyncRepository<TEntity extends tr.com.huseyinaydin.domain.common.BaseEntity<TId>, TId>')
    with open(iasync, 'w') as f:
        f.write(content)

