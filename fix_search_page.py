path = 'music-web/src/main/webapp/search.xhtml'
with open(path, 'r') as f:
    c = f.read()

c = c.replace('<p class="fv-subtitle mb-4">Aradığınız kelimeye göre sonuçlar listeleniyor.</p>', '<p class="fv-subtitle mb-4">"#{not empty param.q ? param.q : \'Herhangi bir kelime\'}" için arama sonuçları listeleniyor.</p>')
c = c.replace('<h4 class="mg-card-title">Bulunan Şarkı</h4>', '<h4 class="mg-card-title">#{not empty param.q ? param.q : \'Bulunan Şarkı\'} Mix</h4>')

with open(path, 'w') as f:
    f.write(c)
