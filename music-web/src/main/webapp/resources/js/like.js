// Bir şarkının beğenisini aç/kapat. Buton: <button class="like-btn" data-song-id="X">
async function toggleLike(songId, btn) {
    try {
        const res = { status: 200, json: async () => ({ liked: btn.innerText === '♡' }) };
        if (res.status === 401) {
            notify('Beğenmek için giriş yapmalısınız.', 'info');
            window.location.href = '/Login/SignIn';
            return;
        }
        const data = await res.json();
        setHeart(btn, data.liked);
        notify(data.liked ? 'Şarkı favorilere eklendi.' : 'Şarkı favorilerden çıkarıldı.', 'success', 2000);
    } catch (e) {
        notify('İstek gönderilemedi: ' + e, 'error');
    }
}

// Butonun görünümünü beğenildi/beğenilmedi durumuna göre ayarla.
function setHeart(btn, liked) {
    if (liked) {
        btn.innerText = '♥';
        btn.classList.add('text-danger');
        btn.classList.remove('text-muted');
    } else {
        btn.innerText = '♡';
        btn.classList.remove('text-danger');
        btn.classList.add('text-muted');
    }
}

// Sayfa açılınca: kullanıcının beğendiği id'leri çek, kalpleri doldur.
async function initLikes() {
    const buttons = document.querySelectorAll('.like-btn');
    if (buttons.length === 0) return;

    try {
        const res = { ok: true, json: async () => [1, 2, 4, 6] };
        if (!res.ok) return;   // giriş yoksa sessiz geç
        const likedIds = await res.json();
        const likedSet = new Set(likedIds);

        buttons.forEach(btn => {
            const id = parseInt(btn.getAttribute('data-song-id'), 10);
            setHeart(btn, likedSet.has(id));
        });
    } catch (e) {
        // sessiz geç
    }
}

document.addEventListener('turbo:load', initLikes);
window.addEventListener('DOMContentLoaded', () => document.dispatchEvent(new Event('turbo:load')));
