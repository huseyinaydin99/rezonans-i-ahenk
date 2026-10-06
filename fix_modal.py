path = 'music-web/src/main/webapp/WEB-INF/layout.xhtml'
with open(path, 'r') as f:
    c = f.read()

modal_html = """
    <!-- Playlist'e ekleme modalı -->
    <div class="pladd-overlay" id="pladdOverlay">
        <div class="pladd-modal" role="dialog" aria-modal="true" aria-labelledby="pladdTitle">
            <div class="pladd-header">
                <h3 class="pladd-title" id="pladdTitle">
                    <span class="material-symbols-outlined">playlist_add</span>
                    Playlist'e Ekle
                </h3>
                <button type="button" class="pladd-close" id="pladdClose" aria-label="Kapat">
                    <span class="material-symbols-outlined">close</span>
                </button>
            </div>
            <div class="pladd-body" id="pladdBody">
                <p class="pladd-status">Playlistler yükleniyor...</p>
            </div>
        </div>
    </div>
"""

c = c.replace('<footer class="sw-footer">', modal_html + '\n    <footer class="sw-footer">')

with open(path, 'w') as f:
    f.write(c)
