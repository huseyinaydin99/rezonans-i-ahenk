#!/bin/bash
PIDS=$(ps aux | grep java | grep tomcat | awk '{print $2}')
if [ -n "$PIDS" ]; then
  kill -9 $PIDS
fi
rm -rf /Users/huseyinaydin99/Desktop/Kiler/servers/apache-tomcat-10.1.60/webapps/ROOT*
rm -rf /Users/huseyinaydin99/Desktop/Kiler/servers/apache-tomcat-10.1.60/webapps/music-web*
rm -rf /Users/huseyinaydin99/Desktop/Kiler/servers/apache-tomcat-10.1.60/webapps/music-credit-system*
cp music-web-thymeleaf/target/music-web-thymeleaf.war /Users/huseyinaydin99/Desktop/Kiler/servers/apache-tomcat-10.1.60/webapps/ROOT.war
> /Users/huseyinaydin99/Desktop/Kiler/servers/apache-tomcat-10.1.60/logs/catalina.out
sh /Users/huseyinaydin99/Desktop/Kiler/servers/apache-tomcat-10.1.60/bin/catalina.sh start
