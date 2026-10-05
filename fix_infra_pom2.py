import xml.etree.ElementTree as ET
import os

pom_path = 'music-infrastructure/pom.xml'
if os.path.exists(pom_path):
    ET.register_namespace('', "http://maven.apache.org/POM/4.0.0")
    tree = ET.parse(pom_path)
    root = tree.getroot()
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
    
    # We will remove the recently added dependencies and add them with versions
    dependencies = root.find('mvn:dependencies', ns)
    for dep in list(dependencies):
        if dep.find('mvn:groupId', ns).text in ['org.springframework.kafka', 'org.springframework.data', 'org.springframework.cloud'] or dep.find('mvn:artifactId', ns).text in ['spring-boot-starter-oauth2-resource-server', 'spring-boot-starter-websocket']:
            dependencies.remove(dep)
            
    deps_to_add = [
        ('org.springframework.kafka', 'spring-kafka', '3.0.7'),
        ('org.springframework.data', 'spring-data-cassandra', '4.1.0'),
        ('org.springframework.data', 'spring-data-elasticsearch', '5.1.0'),
        ('org.springframework.cloud', 'spring-cloud-starter-gateway', '4.0.6'),
        ('org.springframework.boot', 'spring-boot-starter-oauth2-resource-server', '3.1.0'),
        ('org.springframework.boot', 'spring-boot-starter-websocket', '3.1.0')
    ]
    
    for g, a, v in deps_to_add:
        dep = ET.SubElement(dependencies, '{http://maven.apache.org/POM/4.0.0}dependency')
        ET.SubElement(dep, '{http://maven.apache.org/POM/4.0.0}groupId').text = g
        ET.SubElement(dep, '{http://maven.apache.org/POM/4.0.0}artifactId').text = a
        ET.SubElement(dep, '{http://maven.apache.org/POM/4.0.0}version').text = v
            
    tree.write(pom_path, encoding='utf-8', xml_declaration=True)

