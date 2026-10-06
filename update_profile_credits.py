path = 'music-web/src/main/webapp/profile.xhtml'
with open(path, 'r') as f:
    c = f.read()

credit_card = '''
            <div class="pr-stat-card" style="background: rgba(255, 215, 0, 0.1); border: 1px solid rgba(255, 215, 0, 0.3);">
                <div class="pr-stat-icon" style="color: gold;"><span class="material-symbols-outlined">monetization_on</span></div>
                <div>
                    <div class="pr-stat-label" style="color: #ffd700;">Kredi Bakiyesi</div>
                    <div class="pr-stat-value" style="color: #fff;">2,500</div>
                </div>
            </div>
'''

if 'Kredi Bakiyesi' not in c:
    c = c.replace('<div class="pr-stats">', '<div class="pr-stats">\n' + credit_card)
    with open(path, 'w') as f:
        f.write(c)
