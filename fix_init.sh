sed -i '' 's/api.addMapping("\/api\/\*", "\/actuator\/\*", "\/v3\/\*", "\/swagger-ui\/\*", "\/swagger-ui.html", "\/webjars\/\*");/api.addMapping("\/");/g' music-web-thymeleaf/src/main/java/tr/com/huseyinaydin/web/config/WebAppInitializer.java
sed -i '' '/ServletRegistration.Dynamic faces/,/faces.addMapping/d' music-web-thymeleaf/src/main/java/tr/com/huseyinaydin/web/config/WebAppInitializer.java
