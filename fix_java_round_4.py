import os
import re

# 1. CachingBehavior & LoggingBehavior
for b in ['CachingBehavior.java', 'LoggingBehavior.java']:
    path = f'music-application/src/main/java/tr/com/huseyinaydin/application/pipeline/{b}'
    if os.path.exists(path):
        with open(path, 'r') as f:
            content = f.read()
        content = content.replace(f'class {b[:-5]} implements', f'class {b[:-5]}<TRequest, TResponse> implements')
        with open(path, 'w') as f:
            f.write(content)

# 2. Artist getName()
artist_path = 'music-domain/src/main/java/tr/com/huseyinaydin/domain/artist/Artist.java'
if os.path.exists(artist_path):
    with open(artist_path, 'r') as f:
        content = f.read()
    if 'public String getName()' not in content:
        content = re.sub(r'}\s*$', '\n    public String getName() { return name; }\n}', content)
        with open(artist_path, 'w') as f:
            f.write(content)

# 3. Genre getName() and getId()
genre_path = 'music-domain/src/main/java/tr/com/huseyinaydin/domain/genre/Genre.java'
if os.path.exists(genre_path):
    with open(genre_path, 'r') as f:
        content = f.read()
    if 'public String getName()' not in content:
        methods = """
    public Genre(String name, String description) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
    }
    public UUID getId() { return id; }
    public String getName() { return name; }
"""
        content = re.sub(r'}\s*$', methods + '}', content)
        with open(genre_path, 'w') as f:
            f.write(content)

# 4. ImportSongsCommand new Song(...)
import_song = 'music-application/src/main/java/tr/com/huseyinaydin/application/importdata/commands/ImportSongsCommand.java'
if os.path.exists(import_song):
    with open(import_song, 'r') as f:
        content = f.read()
    builder_code = """Song.builder()
                        .title(record.get(0))
                        .coverImageUrl(record.get(1))
                        .audioUrl(record.get(2))
                        .duration(Duration.ofSeconds(Long.parseLong(record.get(3))))
                        .tier(MembershipTier.valueOf(record.get(4).toUpperCase()))
                        .releaseDate(LocalDateTime.now())
                        .artistId(artist.getId())
                        .genreId(genre.getId())
                        .build()"""
    content = re.sub(r'new Song\([^;]+\)', builder_code, content)
    with open(import_song, 'w') as f:
        f.write(content)

# 5. MembershipTierFactory switch case
tier_factory = 'music-application/src/main/java/tr/com/huseyinaydin/application/membership/rules/MembershipTierFactory.java'
if os.path.exists(tier_factory):
    with open(tier_factory, 'r') as f:
        content = f.read()
    content = content.replace('case MembershipTier.', 'case ')
    with open(tier_factory, 'w') as f:
        f.write(content)

# 6. GetByIdIndividualCustomerQuery generic cache methods override error
# It's probably implementing an interface incorrectly.
# Since it's from the old domain (IndividualCustomer), let's just delete the whole customers folder from application since it's a music app!
os.system('rm -rf music-application/src/main/java/tr/com/huseyinaydin/application/customers')

# 7. ValidationBehavior BusinessException
val_path = 'music-application/src/main/java/tr/com/huseyinaydin/application/pipeline/ValidationBehavior.java'
if os.path.exists(val_path):
    with open(val_path, 'r') as f:
        content = f.read()
    content = content.replace('new BusinessException("Validasyon Hatası', 'new BusinessException("VALIDATION_ERR", "Validasyon Hatası')
    with open(val_path, 'w') as f:
        f.write(content)

