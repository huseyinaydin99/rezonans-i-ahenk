sed -i '' 's/<script>/<script>\n\/\/<!\[CDATA\[/g' music-web/src/main/webapp/WEB-INF/layout.xhtml
sed -i '' 's/<\/script>/\/\/\]\]>\n<\/script>/g' music-web/src/main/webapp/WEB-INF/layout.xhtml
