path1 = 'music-web/src/main/webapp/resources/js/like.js'
with open(path1, 'r') as f:
    c1 = f.read()
# Replace fetch for Like Toggle with mock
c1 = c1.replace('''        const res = await fetch('/Like/Toggle?songId=' + songId, { method: 'POST' });''', '''        const res = { status: 200, json: async () => ({ liked: btn.innerText === '♡' }) };''')
c1 = c1.replace('''        const res = await fetch('/Like/LikedIds');''', '''        const res = { ok: true, json: async () => [1, 2, 4, 6] };''')
with open(path1, 'w') as f:
    f.write(c1)

path2 = 'music-web/src/main/webapp/resources/js/playlist-add.js'
with open(path2, 'r') as f:
    c2 = f.read()
c2 = c2.replace('''        const res = await fetch('/Playlist/MyPlaylistsJson');''', '''        const res = { status: 200, json: async () => [{playlistId: 1, name: 'Yol Şarkıları', songCount: 42}, {playlistId: 2, name: 'Spor & Motivasyon', songCount: 18}] };''')
c2 = c2.replace('''        const addRes = await fetch(
            '/Playlist/AddSong?playlistId=' + playlistId + '&songId=' + pladdCurrentSongId,
            { method: 'POST' });''', '''        const addRes = { ok: true };''')
c2 = c2.replace("window.location.href = '/Login/SignIn';", "window.location.href = '/music-credit-system/login.xhtml';")
with open(path2, 'w') as f:
    f.write(c2)

