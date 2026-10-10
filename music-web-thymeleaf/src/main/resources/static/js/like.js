// Bir şarkının beğenisini aç/kapat. Buton: <button class="like-btn" data-song-id="X">
window.toggleLike = async function(songId, btn) {
    if (!songId || songId === 'null' || songId === 'undefined') {
        notify('Şarkı bilgisi okunamadı.', 'error');
        return;
    }

    // Optimistic Update (Tıklandığı an görseli değiştir)
    const isCurrentlyLiked = btn.classList.contains('text-danger');
    setHeart(btn, !isCurrentlyLiked);

    try {
        const res = await fetch('/web-api/Like/Toggle/' + songId, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' }
        });
        
        if (res.status === 401) {
            // Hata varsa geri al
            setHeart(btn, isCurrentlyLiked);
            notify('Beğenmek için giriş yapmalısınız.', 'info');
            window.location.href = '/login';
            return;
        }
        
        if (res.ok) {
            const data = await res.json();
            // Sunucudan gelen kesin duruma eşitle
            setHeart(btn, data.liked);
            notify(data.liked ? 'Şarkı favorilere eklendi.' : 'Şarkı favorilerden çıkarıldı.', 'success', 2000);
        } else {
            // Sunucu hatası varsa geri al
            setHeart(btn, isCurrentlyLiked);
            notify('İşlem başarısız oldu.', 'error');
        }
    } catch (e) {
        // Ağ hatası varsa geri al
        setHeart(btn, isCurrentlyLiked);
        notify('Sunucuya bağlanılamadı.', 'error');
    }
}

// Butonun görünümünü beğenildi/beğenilmedi durumuna göre ayarla.
function setHeart(btn, liked) {
    if (!btn) return;
    if (liked) {
        btn.innerHTML = '♥';
        btn.classList.add('text-danger');
        btn.classList.remove('text-muted');
        btn.title = 'Beğeniyi kaldır';
    } else {
        btn.innerHTML = '♡';
        btn.classList.remove('text-danger');
        btn.classList.add('text-muted');
        btn.title = 'Beğen';
    }
}

// Sayfa açılınca: kullanıcının beğendiği id'leri çek, kalpleri doldur.
window.initLikes = async function() {
    const buttons = document.querySelectorAll('.like-btn');
    if (buttons.length === 0) return;

    try {
        const res = await fetch('/web-api/Like/MyLikes');
        if (!res.ok) return;   // giriş yoksa sessiz geç
        
        const likedIds = await res.json();
        const likedSet = new Set(likedIds);

        buttons.forEach(btn => {
            const idAttr = btn.getAttribute('data-song-id');
            if (idAttr) {
                const id = parseInt(idAttr, 10);
                setHeart(btn, likedSet.has(id));
            }
        });
    } catch (e) {
        console.error("Favoriler yüklenemedi", e);
    }
}

// Event bağlamaları (Global yapılar)
document.addEventListener('turbo:load', initLikes);
window.addEventListener('DOMContentLoaded', () => {
    if (typeof Turbo === 'undefined') initLikes();
});
