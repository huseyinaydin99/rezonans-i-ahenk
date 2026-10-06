sed -i '' 's/<script type="module">import hotwiredTurbo from "https:\/\/cdn.skypack.dev\/@hotwired\/turbo";<\/script>/<script src="https:\/\/unpkg.com\/@hotwired\/turbo@8.0.4\/dist\/turbo.es2017-umd.js"><\/script>/' music-web/src/main/webapp/WEB-INF/layout.xhtml

sed -i '' 's/data-turbo-permanent="true"/data-turbo-permanent/g' music-web/src/main/webapp/WEB-INF/layout.xhtml

sed -i '' '/<form class="sw-search" action="search.xhtml" method="get">/,/<\/form>/c\
                <form class="sw-search" action="search.xhtml" method="get" style="position: relative;">\
                    <input type="search" id="searchInput" name="q" placeholder="Şarkı, sanatçı, tür ara..." autocomplete="off" />\
                    <button type="submit">Ara</button>\
                    <div id="searchAutocomplete" style="display: none; position: absolute; top: 100%; left: 0; right: 0; background: #1a1e29; border: 1px solid #333; border-radius: 4px; z-index: 1000; max-height: 300px; overflow-y: auto; margin-top: 4px; box-shadow: 0 4px 12px rgba(0,0,0,0.5);">\
                    </div>\
                </form>
' music-web/src/main/webapp/WEB-INF/layout.xhtml

sed -i '' '/<\/h:body>/i\
<script>\
document.addEventListener("turbo:load", () => {\
    const input = document.getElementById("searchInput");\
    const dropdown = document.getElementById("searchAutocomplete");\
    if (!input || !dropdown) return;\
    let debounceTimer;\
    input.addEventListener("input", (e) => {\
        clearTimeout(debounceTimer);\
        const q = e.target.value.trim();\
        if (q.length < 2) {\
            dropdown.style.display = "none";\
            return;\
        }\
        debounceTimer = setTimeout(async () => {\
            try {\
                const res = await fetch("/music-credit-system/api/Song/Search?q=" + encodeURIComponent(q));\
                if (res.ok) {\
                    const data = await res.json();\
                    if (data.length > 0) {\
                        let html = "";\
                        data.forEach(song => {\
                            html += `<a href="search.xhtml?q=${encodeURIComponent(song.title)}" style="display: block; padding: 10px; color: #fff; text-decoration: none; border-bottom: 1px solid #333;">\
                                <div style="font-weight: bold; font-size: 0.9em; display: flex; align-items: center; justify-content: space-between;">\
                                    <span>${song.title}</span>\
                                    <span style="background: rgba(255,215,0,0.2); color: gold; padding: 2px 6px; border-radius: 4px; font-size: 0.7em;">${song.tier}</span>\
                                </div>\
                                <div style="font-size: 0.8em; color: #aaa;">${song.artist}</div>\
                            </a>`;\
                        });\
                        dropdown.innerHTML = html;\
                        dropdown.style.display = "block";\
                    } else {\
                        dropdown.style.display = "none";\
                    }\
                }\
            } catch (err) { console.error(err); }\
        }, 300);\
    });\
    document.addEventListener("click", (e) => {\
        if (!input.contains(e.target) && !dropdown.contains(e.target)) {\
            dropdown.style.display = "none";\
        }\
    });\
});\
</script>
' music-web/src/main/webapp/WEB-INF/layout.xhtml
