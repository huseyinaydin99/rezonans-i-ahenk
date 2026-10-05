import xml.etree.ElementTree as ET
import os

pom_path = 'music-web/pom.xml'
if os.path.exists(pom_path):
    ET.register_namespace('', "http://maven.apache.org/POM/4.0.0")
    tree = ET.parse(pom_path)
    root = tree.getroot()
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
    
    dependencies = root.find('mvn:dependencies', ns)
    
    deps_to_add = [
        ('jakarta.enterprise', 'jakarta.enterprise.cdi-api', '4.0.1')
    ]
    
    for g, a, v in deps_to_add:
        dep = ET.SubElement(dependencies, '{http://maven.apache.org/POM/4.0.0}dependency')
        ET.SubElement(dep, '{http://maven.apache.org/POM/4.0.0}groupId').text = g
        ET.SubElement(dep, '{http://maven.apache.org/POM/4.0.0}artifactId').text = a
        ET.SubElement(dep, '{http://maven.apache.org/POM/4.0.0}version').text = v
            
    tree.write(pom_path, encoding='utf-8', xml_declaration=True)

