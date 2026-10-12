// Bir şarkıyı playlist'e ekleme akışı — ortada açılan modal üzerinden.
// 1) modalı aç, 2) kullanıcının playlist'lerini çekip listele, 3) seçilene ekle.

let pladdCurrentSongId = null;

// Şarkı kartlarındaki "Playlist'e ekle" butonları bunu çağırır.
function addToPlaylist(songId) {
    pladdCurrentSongId = songId;

    const overlay = document.getElementById('pladdOverlay');
    const body = document.getElementById('pladdBody');
    if (!overlay || !body) return; // modal bu sayfada yoksa sessizce çık

    overlay.classList.add('show');
    document.body.classList.add('pladd-open');
    body.innerHTML = '<p class="pladd-status">Playlistler yükleniyor...</p>';

    loadPlaylistOptions();
}

function closePlaylistModal() {
    const overlay = document.getElementById('pladdOverlay');
    if (!overlay) return;
    overlay.classList.remove('show');
    document.body.classList.remove('pladd-open');
    pladdCurrentSongId = null;
}

async function loadPlaylistOptions() {
    const body = document.getElementById('pladdBody');
    if (!body) return;

    try {
        let apiUrl = window.getAppUrl ? window.getAppUrl('/web-api/playlists') : '/web-api/playlists';
        let res = await fetch(apiUrl, { credentials: 'same-origin' });
        
        if (res.status === 404) {
            apiUrl = window.getAppUrl ? window.getAppUrl('/api/v1/playlists') : '/api/v1/playlists';
            res = await fetch(apiUrl, { credentials: 'same-origin' });
        }

        if (res.status === 401 || res.status === 403) {
            closePlaylistModal();
            notify('Playlist için giriş yapmalısınız.', 'info');
            window.location.href = window.getAppUrl ? window.getAppUrl('/login') : '/music-credit-system/login.xhtml';
            return;
        }

        if (!res.ok) {
            throw new Error('HTTP ' + res.status);
        }

        const playlists = await res.json();

        if (!playlists || playlists.length === 0) {
            body.innerHTML =
                '<div class="pladd-empty">' +
                '<span class="material-symbols-outlined">queue_music</span>' +
                '<p>Henüz playlistin yok.</p>' +
                '<a href="/Playlist/Index" class="pladd-empty-btn">' +
                '<span class="material-symbols-outlined">add</span>Playlist Oluştur</a>' +
                '</div>';
            return;
        }

        body.innerHTML = '<div class="pladd-list"></div>';
        const list = body.querySelector('.pladd-list');

        playlists.forEach(function (p) {
            const item = document.createElement('button');
            item.type = 'button';
            item.className = 'pladd-item';
            item.innerHTML =
                '<span class="pladd-item-icon material-symbols-outlined">queue_music</span>' +
                '<span class="pladd-item-info">' +
                '<span class="pladd-item-name"></span>' +
                '<span class="pladd-item-count">' + (p.songCount != null ? p.songCount : 0) + ' şarkı</span>' +
                '</span>' +
                '<span class="pladd-item-arrow material-symbols-outlined">chevron_right</span>';
            item.querySelector('.pladd-item-name').textContent = p.name;
            item.addEventListener('click', function () {
                selectPlaylist(p.playlistId, p.name, item);
            });
            list.appendChild(item);
        });
    } catch (e) {
        console.error('[PlaylistModal] Yükleme hatası:', e);
        body.innerHTML = '<p class="pladd-status pladd-status--error">Playlistler yüklenemedi.</p>';
    }
}

async function selectPlaylist(playlistId, playlistName, itemEl) {
    if (!pladdCurrentSongId) return;

    const items = document.querySelectorAll('.pladd-item');
    items.forEach(function (el) { el.disabled = true; });
    if (itemEl) itemEl.classList.add('pladd-item--loading');

    try {
        let addUrl = window.getAppUrl
            ? window.getAppUrl('/web-api/playlists/' + encodeURIComponent(playlistId) + '/songs/' + encodeURIComponent(pladdCurrentSongId))
            : ('/web-api/playlists/' + encodeURIComponent(playlistId) + '/songs/' + encodeURIComponent(pladdCurrentSongId));

        let addRes = await fetch(addUrl, {
            method: 'POST',
            credentials: 'same-origin'
        });

        if (addRes.status === 404) {
            addUrl = window.getAppUrl
                ? window.getAppUrl('/api/v1/playlists/' + encodeURIComponent(playlistId) + '/songs/' + encodeURIComponent(pladdCurrentSongId))
                : ('/api/v1/playlists/' + encodeURIComponent(playlistId) + '/songs/' + encodeURIComponent(pladdCurrentSongId));
            addRes = await fetch(addUrl, {
                method: 'POST',
                credentials: 'same-origin'
            });
        }

        if (addRes.status === 401 || addRes.status === 403) {
            closePlaylistModal();
            notify('Playlist için giriş yapmalısınız.', 'info');
            window.location.href = window.getAppUrl ? window.getAppUrl('/login') : '/music-credit-system/login.xhtml';
            return;
        }

        if (addRes.status === 409) {
            closePlaylistModal();
            notify('Bu şarkı zaten "' + playlistName + '" playlistine eklenmiş.', 'info');
            return;
        }

        if (addRes.ok) {
            notify('Şarkı "' + playlistName + '" playlistine eklendi.', 'success');
            closePlaylistModal();
        } else {
            console.error('[PlaylistModal] Şarkı ekleme hatası HTTP:', addRes.status);
            notify('Şarkı eklenemedi.', 'error');
            items.forEach(function (el) { el.disabled = false; });
            if (itemEl) itemEl.classList.remove('pladd-item--loading');
        }
    } catch (e) {
        console.error('[PlaylistModal] İstek hatası:', e);
        notify('İstek gönderilemedi: ' + e, 'error');
        items.forEach(function (el) { el.disabled = false; });
        if (itemEl) itemEl.classList.remove('pladd-item--loading');
    }
}

// Modal kapatma etkileşimleri: kapat butonu, dış alana tıklama, ESC tuşu.
document.addEventListener('DOMContentLoaded', function () {
    const overlay = document.getElementById('pladdOverlay');
    if (!overlay) return;

    const closeBtn = document.getElementById('pladdClose');
    if (closeBtn) closeBtn.addEventListener('click', closePlaylistModal);

    overlay.addEventListener('click', function (e) {
        if (e.target === overlay) closePlaylistModal();
    });

    document.addEventListener('keydown', function (e) {
        if (e.key === 'Escape' && overlay.classList.contains('show')) closePlaylistModal();
    });
});

window.addEventListener('DOMContentLoaded', () => document.dispatchEvent(new Event('turbo:load')));
