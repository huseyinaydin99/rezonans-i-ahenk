path = 'music-web/src/main/webapp/search.xhtml'
with open(path, 'r') as f:
    c = f.read()

replacement = '''
        <div id="searchResults" class="mg-grid mt-4">
            <div class="text-secondary">Aranıyor...</div>
        </div>
'''

import re
c = re.sub(r'<div class="alert alert-info.*?</div>.*?<div class="mg-grid mt-4">.*?</div>\s*</div>', replacement.strip(), c, flags=re.DOTALL)

script = '''
    <ui:define name="scripts">
        <script>
            document.addEventListener('turbo:load', async function() {
                const urlParams = new URLSearchParams(window.location.search);
                const q = urlParams.get('q');
                
                const resultsDiv = document.getElementById('searchResults');
                if (!resultsDiv) return; // Not on search page
                
                try {
                    const res = await fetch('/music-credit-system/api/Song/Search?q=' + (q || ''));
                    if (!res.ok) throw new Error("Arama yapılamadı");
                    
                    const data = await res.json();
                    
                    if (data.length === 0) {
                        resultsDiv.innerHTML = '<div class="alert alert-warning" style="background: rgba(255, 193, 7, 0.1); border: 1px solid rgba(255, 193, 7, 0.3); color: #ffc107;">Aradığınız kriterlere uygun şarkı bulunamadı.</div>';
                        return;
                    }
                    
                    let html = '';
                    data.forEach(song => {
                        let tierClass = 'mg-card--tier-1';
                        if (song.tier === 'Premium') tierClass = 'mg-card--tier-2';
                        if (song.tier === 'Elit') tierClass = 'mg-card--tier-3';
                        
                        html += `
                        <div class="mg-card ${tierClass}">
                            <div class="mg-card-cover">
                                <img src="${song.cover}" alt="${song.title}" />
                                <div class="mg-card-play-overlay">
                                    <button class="mg-card-play queue-item" data-song-id="${song.id}" data-song-title="${song.title}" data-song-artist="${song.artist}" onclick="player.playFrom(${song.id})" title="Oynat">
                                        <span class="material-symbols-outlined" style="font-variation-settings: 'FILL' 1;">play_arrow</span>
                                    </button>
                                </div>
                            </div>
                            <div class="mg-card-title-row">
                                <h4 class="mg-card-title">${song.title}</h4>
                                <span class="mg-badge">${song.tier}</span>
                            </div>
                            <p class="mg-card-artist">${song.artist}</p>
                            <div class="mg-card-actions">
                                <button class="mg-icon-btn like-btn text-muted" data-song-id="${song.id}" onclick="toggleLike(${song.id}, this)" title="Beğen">&#9825;</button>
                                <button class="mg-icon-btn" onclick="addToPlaylist(${song.id})" title="Playlist'e ekle">
                                    <span class="material-symbols-outlined">playlist_add</span>
                                </button>
                            </div>
                        </div>
                        `;
                    });
                    
                    resultsDiv.innerHTML = html;
                } catch (e) {
                    resultsDiv.innerHTML = '<div class="alert alert-danger" style="background: rgba(220, 53, 69, 0.1); border: 1px solid rgba(220, 53, 69, 0.3); color: #dc3545;">Arama sırasında bir hata oluştu: ' + e + '</div>';
                }
            });
        </script>
    </ui:define>
</ui:composition>
'''

c = c.replace('</ui:composition>', script)

with open(path, 'w') as f:
    f.write(c)
