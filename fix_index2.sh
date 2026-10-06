sed -i '' 's/<ui:repeat value="#{homeBean.dashboardData.personalizedRecommendations}" var="song">/<th:block th:each="song : ${personalizedRecommendations}">/g' music-web-thymeleaf/src/main/resources/templates/index.html
sed -i '' 's/<\/ui:repeat>/<\/th:block>/g' music-web-thymeleaf/src/main/resources/templates/index.html
sed -i '' 's/<ui:repeat value="#{homeBean.dashboardData.topGlobalSongs}" var="song">/<th:block th:each="song : ${topGlobalSongs}">/g' music-web-thymeleaf/src/main/resources/templates/index.html
sed -i '' 's/#{song.title}/${song.title}/g' music-web-thymeleaf/src/main/resources/templates/index.html
sed -i '' 's/#{song.songId}/${song.songId}/g' music-web-thymeleaf/src/main/resources/templates/index.html
sed -i '' 's/#{song.artist}/${song.artist}/g' music-web-thymeleaf/src/main/resources/templates/index.html
