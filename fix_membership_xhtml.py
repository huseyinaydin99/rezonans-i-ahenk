path = 'music-web/src/main/webapp/membership.xhtml'
with open(path, 'r') as f:
    c = f.read()

script = '''
    <ui:define name="scripts">
        <script>
            async function buyPackage(tier) {
                try {
                    const res = await fetch('/music-credit-system/api/Membership/Buy?tier=' + tier, { method: 'POST', credentials: 'same-origin' });
                    if (res.ok) {
                        const data = await res.json();
                        document.getElementById('currentTier').innerText = data.tier;
                        notify(data.message + ' Artık müzik sayfasında bu kademedeki şarkıları çalabilirsiniz.', 'success');
                    } else {
                        const data = await res.json();
                        if (res.status === 401) {
                            window.location.href = 'login.xhtml';
                        } else {
                            notify(data.message || 'Satın alma başarısız.', 'error');
                        }
                    }
                } catch (e) {
                    notify('İstek gönderilemedi: ' + e, 'error');
                }
            }
        </script>
    </ui:define>
</ui:composition>
'''

c = c.replace('onclick="alert(\'Demo: Paket başarıyla alındı!\')"', 'onclick="buyPackage(this.closest(\'.pk-card\').querySelector(\'.pk-card-name\').innerText)"')
c = c.replace('</ui:composition>', script)

with open(path, 'w') as f:
    f.write(c)
