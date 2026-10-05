import xml.etree.ElementTree as ET

def add_snakeyaml():
    tree = ET.parse('music-web/pom.xml')
    root = tree.getroot()
    ns = {'mvn': 'http://maven.apache.org/POM/4.0.0'}
    ET.register_namespace('', 'http://maven.apache.org/POM/4.0.0')
    
    dependencies = root.find('mvn:dependencies', ns)
    if dependencies is None:
        dependencies = ET.SubElement(root, 'dependencies')
        
    dep = ET.SubElement(dependencies, 'dependency')
    ET.SubElement(dep, 'groupId').text = 'org.yaml'
    ET.SubElement(dep, 'artifactId').text = 'snakeyaml'
    ET.SubElement(dep, 'version').text = '2.2'
    
    tree.write('music-web/pom.xml', xml_declaration=True, encoding='UTF-8')

add_snakeyaml()
