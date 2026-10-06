path = 'music-web/src/main/webapp/index.xhtml'
with open(path, 'r') as f:
    c = f.read()

target_strip = '''                <div class="mg-hero-strip" style="display: flex; overflow-x: auto; gap: 1rem; scrollbar-width: none;">
                    <ui:repeat value="#{homeBean.dashboardData.personalizedRecommendations}" var="song">
                        <div class="mg-card mg-card--tier-1" style="min-width: 200px;">
                            <div class="mg-card-cover">
                                <img src="#{song.coverUrl != null ? song.coverUrl : 'https://via.placeholder.com/150'}" alt="#{song.title}" style="width: 100%; border-radius: 8px;"/>
                            </div>
                            <div class="mg-card-title-row mt-2">
                                <h4 class="mg-card-title text-white">#{song.title}</h4>
                            </div>
                            <p class="mg-card-artist text-secondary">#{song.artist}</p>
                        </div>
                    </ui:repeat>
                </div>'''

replacement_strip = '''                <div class="mg-hero-strip">
                    <ui:repeat value="#{homeBean.dashboardData.personalizedRecommendations}" var="song">
                        <div class="mg-card mg-card--tier-1">
                            <div class="mg-card-cover">
                                <img src="#{song.coverUrl != null ? song.coverUrl : 'https://via.placeholder.com/150'}" alt="#{song.title}" />
                                <div class="mg-card-play-overlay">
                                    <button class="mg-card-play queue-item" 
                                            data-song-id="#{song.songId}" 
                                            data-song-title="#{song.title}" 
                                            data-song-artist="#{song.artist}" 
                                            onclick="player.playFrom(#{song.songId})" 
                                            title="Oynat">
                                        <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">play_arrow</span>
                                    </button>
                                </div>
                            </div>
                            <div class="mg-card-title-row">
                                <h4 class="mg-card-title">#{song.title}</h4>
                                <span class="mg-badge">Basic</span>
                            </div>
                            <p class="mg-card-artist">#{song.artist}</p>
                            <div class="mg-card-actions">
                                <button class="mg-icon-btn like-btn text-muted" data-song-id="#{song.songId}" onclick="toggleLike(#{song.songId}, this)" title="Beğen">&#9825;</button>
                                <button class="mg-icon-btn" onclick="addToPlaylist(#{song.songId})" title="Playlist'e ekle">
                                    <span class="material-symbols-outlined">playlist_add</span>
                                </button>
                            </div>
                        </div>
                    </ui:repeat>
                </div>'''

c = c.replace(target_strip, replacement_strip)

target_grid = '''            <div class="mg-grid" style="display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 1.5rem;">
                <ui:repeat value="#{homeBean.dashboardData.topGlobalSongs}" var="song">
                    <div class="mg-card mg-card--tier-2">
                        <div class="mg-card-cover">
                            <img src="#{song.coverUrl != null ? song.coverUrl : 'https://via.placeholder.com/150'}" alt="#{song.title}" style="width: 100%; border-radius: 8px;"/>
                            <div class="mg-card-play-overlay">
                                <button class="mg-card-play queue-item" title="Oynat">
                                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">play_arrow</span>
                                </button>
                            </div>
                        </div>
                        <div class="mg-card-title-row mt-2">
                            <h4 class="mg-card-title text-white">#{song.title}</h4>
                            <span class="mg-badge bg-warning text-dark px-2 rounded">Gold</span>
                        </div>
                        <p class="mg-card-artist text-secondary">#{song.artist}</p>
                        <div class="mg-card-actions d-flex justify-content-between mt-2">
                            <button class="btn btn-link text-muted p-0" title="Beğen">♡</button>
                            <button class="btn btn-link text-info p-0" title="Playlist'e ekle">
                                <span class="material-symbols-outlined">playlist_add</span>
                            </button>
                        </div>
                    </div>
                </ui:repeat>
            </div>'''

replacement_grid = '''            <div class="mg-grid">
                <ui:repeat value="#{homeBean.dashboardData.topGlobalSongs}" var="song">
                    <div class="mg-card mg-card--tier-2">
                        <div class="mg-card-cover">
                            <img src="#{song.coverUrl != null ? song.coverUrl : 'https://via.placeholder.com/150'}" alt="#{song.title}" />
                            <div class="mg-card-play-overlay">
                                <button class="mg-card-play queue-item" 
                                        data-song-id="#{song.songId}" 
                                        data-song-title="#{song.title}" 
                                        data-song-artist="#{song.artist}" 
                                        onclick="player.playFrom(#{song.songId})" 
                                        title="Oynat">
                                    <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">play_arrow</span>
                                </button>
                            </div>
                        </div>
                        <div class="mg-card-title-row">
                            <h4 class="mg-card-title">#{song.title}</h4>
                            <span class="mg-badge">Gold</span>
                        </div>
                        <p class="mg-card-artist">#{song.artist}</p>
                        <div class="mg-card-actions">
                            <button class="mg-icon-btn like-btn text-muted" data-song-id="#{song.songId}" onclick="toggleLike(#{song.songId}, this)" title="Beğen">&#9825;</button>
                            <button class="mg-icon-btn" onclick="addToPlaylist(#{song.songId})" title="Playlist'e ekle">
                                <span class="material-symbols-outlined">playlist_add</span>
                            </button>
                        </div>
                    </div>
                </ui:repeat>
            </div>'''

c = c.replace(target_grid, replacement_grid)

with open(path, 'w') as f:
    f.write(c)
