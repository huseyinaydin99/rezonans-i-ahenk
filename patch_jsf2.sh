sed -i '' '/<script>/,/<\/script>/d' music-web/src/main/webapp/WEB-INF/layout.xhtml

sed -i '' '/<\/h:body>/i\
<script>\
//<![CDATA[\
document.getElementById("swNavToggle")?.addEventListener("click", function () {\
    document.getElementById("swNavLinks")?.classList.toggle("show");\
});\
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
                            html += "<a href=\\"search.xhtml?q=" + encodeURIComponent(song.title) + "\\" style=\\"display: block; padding: 10px; color: #fff; text-decoration: none; border-bottom: 1px solid #333;\\">" +\
                                "<div style=\\"font-weight: bold; font-size: 0.9em; display: flex; align-items: center; justify-content: space-between;\\">" +\
                                    "<span>" + song.title + "</span>" +\
                                    "<span style=\\"background: rgba(255,215,0,0.2); color: gold; padding: 2px 6px; border-radius: 4px; font-size: 0.7em;\\">" + song.tier + "</span>" +\
                                "</div>" +\
                                "<div style=\\"font-size: 0.8em; color: #aaa;\\">" + song.artist + "</div>" +\
                            "</a>";\
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
//]]>\
</script>
' music-web/src/main/webapp/WEB-INF/layout.xhtml
