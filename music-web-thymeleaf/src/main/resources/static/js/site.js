function initAutocomplete() {
    const searchInput = document.getElementById('searchInput');
    const autocompleteBox = document.getElementById('searchAutocomplete');

    if (!searchInput || !autocompleteBox) return;
    if (searchInput.dataset.autocompleteReady === 'true') return;
    searchInput.dataset.autocompleteReady = 'true';

    const searchForm = searchInput.closest('form');
    const baseApi = searchForm?.getAttribute('data-search-api') || (window.getAppUrl ? window.getAppUrl('/api/Song/Search') : '/api/Song/Search');
    const baseSearchPage = searchForm?.getAttribute('data-search-page') || (window.getAppUrl ? window.getAppUrl('/search') : '/search');

    let debounceTimer;

    searchInput.addEventListener('input', function() {
        clearTimeout(debounceTimer);
        const query = this.value.trim();

        if (query.length < 2) {
            autocompleteBox.style.display = 'none';
            autocompleteBox.innerHTML = '';
            return;
        }

        debounceTimer = setTimeout(async () => {
            try {
                const res = await fetch(baseApi + '?q=' + encodeURIComponent(query));
                if (!res.ok) throw new Error('Arama hatası (' + res.status + ')');
                
                const data = await res.json();
                
                if (!data || data.length === 0) {
                    autocompleteBox.innerHTML = '<div style="padding: 12px; color: #888; text-align: center; font-size: 13px;">Sonuç bulunamadı</div>';
                    autocompleteBox.style.display = 'block';
                    return;
                }

                let html = '';
                data.forEach(song => {
                    const tierBadge = song.tier ? `<span style="font-size: 10px; background: rgba(0, 255, 204, 0.2); color: #00ffcc; padding: 2px 6px; border-radius: 4px; margin-left: 6px;">${song.tier}</span>` : '';
                    const genreInfo = song.genre ? `<span style="color: #6c757d; font-size: 11px; margin-left: 6px;">• ${song.genre}</span>` : '';
                    const defaultSvg = window.getAppUrl ? window.getAppUrl('/images/default-cover.svg') : '/images/default-cover.svg';
                    const coverImg = song.cover || defaultSvg;
                    const targetUrl = baseSearchPage + '?q=' + encodeURIComponent(song.title);

                    html += `
                        <a href="${targetUrl}" style="display: flex; align-items: center; padding: 10px 14px; text-decoration: none; border-bottom: 1px solid #2a2e39; transition: background 0.2s;">
                            <img src="${coverImg}" alt="${song.title}" onerror="window.onImageError(this, 'song')" style="width: 40px; height: 40px; border-radius: 6px; object-fit: cover; margin-right: 12px; flex-shrink: 0;">
                            <div style="flex: 1; min-width: 0;">
                                <div style="color: #fff; font-weight: 600; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                    ${song.title} ${tierBadge}
                                </div>
                                <div style="color: #9aa0a6; font-size: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                    ${song.artist} ${genreInfo}
                                </div>
                            </div>
                        </a>
                    `;
                });

                autocompleteBox.innerHTML = html;
                autocompleteBox.style.display = 'block';
                
                const items = autocompleteBox.querySelectorAll('a');
                items.forEach(item => {
                    item.addEventListener('mouseenter', () => item.style.background = 'rgba(255,255,255,0.06)');
                    item.addEventListener('mouseleave', () => item.style.background = 'transparent');
                });

            } catch (error) {
                console.error("Autocomplete error:", error);
            }
        }, 250); // 250ms debounce
    });

    // Dışarı tıklayınca kapatma
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !autocompleteBox.contains(e.target)) {
            autocompleteBox.style.display = 'none';
        }
    });
    
    // İnputa tekrar tıklanırsa eğer içi doluysa aç
    searchInput.addEventListener('focus', function() {
        if (this.value.trim().length >= 2 && autocompleteBox.innerHTML !== '') {
            autocompleteBox.style.display = 'block';
        }
    });
}

// --- Global Image Fallback & Rescue Handler ---
window.onImageError = function(img, type) {
    if (!img || img.dataset.fallbackApplied === 'true') return;
    img.dataset.fallbackApplied = 'true';
    const fallbackPath = (type === 'artist') ? '/images/default-artist.svg' : '/images/default-cover.svg';
    const resolvedUrl = window.getAppUrl ? window.getAppUrl(fallbackPath) : fallbackPath;
    img.src = resolvedUrl;
};

// Global capture-phase error listener for images (image error events do not bubble)
document.addEventListener('error', function(event) {
    if (event.target && event.target.tagName === 'IMG') {
        const img = event.target;
        if (img.dataset.fallbackApplied === 'true') return;
        const isArtist = img.classList.contains('al-avatar') ||
                         img.classList.contains('hero-avatar') ||
                         img.closest('.al-avatar') !== null ||
                         (img.alt && img.alt.toLowerCase().includes('sanatçı'));
        window.onImageError(img, isArtist ? 'artist' : 'song');
    }
}, true);

// Güvenli başlatıcı: hem DOMContentLoaded, hem Turbo, hem de direkt hazır olma durumunu kapsar
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', initAutocomplete);
} else {
    initAutocomplete();
}
document.addEventListener('turbo:load', initAutocomplete);

