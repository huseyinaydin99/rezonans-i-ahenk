import os

orm_path = 'music-domain/src/main/resources/META-INF/orm/BaseEntity.xml'
if os.path.exists(orm_path):
    with open(orm_path, 'r') as f:
        content = f.read()
    
    # We will swap version and basic
    # Find version block
    version_block = """<version name="version">
                <column name="VERSION" nullable="false" column-definition="BIGINT DEFAULT 0"/>
            </version>"""
    
    basic_blocks = """<basic name="createdDate">
                <column name="CREATED_DATE" nullable="false" updatable="false"/>
            </basic>
            <basic name="updatedDate">
                <column name="UPDATED_DATE" nullable="true"/>
            </basic>
            <basic name="deletedDate">
                <column name="DELETED_DATE" nullable="true"/>
            </basic>"""
            
    # The current order is id -> version -> basic. Let's make it id -> basic -> version
    content = content.replace(version_block + '\n            ' + basic_blocks, basic_blocks + '\n            ' + version_block)
    
    with open(orm_path, 'w') as f:
        f.write(content)
