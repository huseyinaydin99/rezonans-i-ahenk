document.addEventListener('turbo:load', initAutocomplete);
window.addEventListener('DOMContentLoaded', () => {
    if (typeof Turbo === 'undefined') initAutocomplete();
});

function initAutocomplete() {
    const searchInput = document.getElementById('searchInput');
    const autocompleteBox = document.getElementById('searchAutocomplete');

    if (!searchInput || !autocompleteBox) return;

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
                const res = await fetch('/api/Song/Search?q=' + encodeURIComponent(query));
                if (!res.ok) throw new Error('Arama hatası');
                
                const data = await res.json();
                
                if (data.length === 0) {
                    autocompleteBox.innerHTML = '<div style="padding: 10px; color: #888; text-align: center;">Sonuç bulunamadı</div>';
                    autocompleteBox.style.display = 'block';
                    return;
                }

                let html = '';
                data.forEach(song => {
                    const tierBadge = song.tier ? `<span style="font-size: 10px; background: rgba(0, 255, 204, 0.2); color: #00ffcc; padding: 2px 6px; border-radius: 4px; margin-left: 6px;">${song.tier}</span>` : '';
                    html += `
                        <a href="/search?q=${encodeURIComponent(song.title)}" style="display: flex; align-items: center; padding: 10px; text-decoration: none; border-bottom: 1px solid #333; transition: background 0.2s;">
                            <img src="${song.cover}" alt="${song.title}" style="width: 40px; height: 40px; border-radius: 4px; object-fit: cover; margin-right: 12px;">
                            <div style="flex: 1; overflow: hidden;">
                                <div style="color: #fff; font-weight: 500; font-size: 14px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                    ${song.title} ${tierBadge}
                                </div>
                                <div style="color: #888; font-size: 12px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                    ${song.artist}
                                </div>
                            </div>
                        </a>
                    `;
                });

                autocompleteBox.innerHTML = html;
                autocompleteBox.style.display = 'block';
                
                // Hover efektini CSS yerine JS ile ekliyoruz (inline style kullandığımız için)
                const items = autocompleteBox.querySelectorAll('a');
                items.forEach(item => {
                    item.addEventListener('mouseenter', () => item.style.background = 'rgba(255,255,255,0.05)');
                    item.addEventListener('mouseleave', () => item.style.background = 'transparent');
                });

            } catch (error) {
                console.error("Autocomplete error:", error);
            }
        }, 350); // 350ms debounce
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
