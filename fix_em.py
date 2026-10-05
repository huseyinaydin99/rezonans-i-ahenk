import re
with open('music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java', 'r') as f:
    content = f.read()

import_statement = "import jakarta.persistence.EntityManager;\nimport jakarta.persistence.EntityManagerFactory;\n"
if "import jakarta.persistence.EntityManager;" not in content:
    content = content.replace("import javax.sql.DataSource;", import_statement + "import javax.sql.DataSource;")

bean_definition = """
    @Bean
    public EntityManager entityManager(EntityManagerFactory entityManagerFactory) {
        return org.springframework.orm.jpa.SharedEntityManagerCreator.createSharedEntityManager(entityManagerFactory);
    }
"""

if "public EntityManager entityManager(" not in content:
    content = content.replace("public LocalContainerEntityManagerFactoryBean entityManagerFactory(", bean_definition + "\n    @Bean\n    public LocalContainerEntityManagerFactoryBean entityManagerFactory(")

with open('music-persistence/src/main/java/tr/com/huseyinaydin/persistence/config/PersistenceConfig.java', 'w') as f:
    f.write(content)
