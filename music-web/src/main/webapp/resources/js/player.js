/*
 * Rezonans-ı Ahenk — Global Oynatıcı
 *
 * Sorumluluklar:
 *  1. Özel kontroller (oynat/duraklat, konum, ses, sessiz, klavye kısayolları)
 *  2. Sayfa geçişlerinde kesintisizlik: konum ve ses seviyesi localStorage'da tutulur,
 *     aynı şarkıya dönüldüğünde kalınan saniyeden devam edilir.
 *  3. Dinleme telemetrisi: STARTED / PROGRESS / PAUSED / SKIPPED / COMPLETED
 *     durumları /api/v1/playback/heartbeat'e gönderilir -> Kafka -> Cassandra & Spark.
 */
(function (window, document) {
    "use strict";

    var HEARTBEAT_INTERVAL_MS = 15000;
    var STORAGE_KEY = "rz.player.state";

    var el = {};
    var ctx = null;            // { songId, userId, token, url, duration }
    var heartbeatTimer = null;
    var terminalReported = false;
    var isSeeking = false;
    var listenersBound = false;

    // ---------- Yardımcılar ----------

    function formatTime(sec) {
        if (!isFinite(sec) || sec < 0) { sec = 0; }
        var m = Math.floor(sec / 60);
        var s = Math.floor(sec % 60);
        return m + ":" + (s < 10 ? "0" : "") + s;
    }

    function loadState() {
        try { return JSON.parse(window.localStorage.getItem(STORAGE_KEY)) || {}; }
        catch (e) { return {}; }
    }

    function saveState(patch) {
        var state = loadState();
        for (var k in patch) { if (patch.hasOwnProperty(k)) { state[k] = patch[k]; } }
        try { window.localStorage.setItem(STORAGE_KEY, JSON.stringify(state)); } catch (e) { /* kota dolu */ }
    }

    function durationSeconds() {
        var d = el.audio && isFinite(el.audio.duration) ? el.audio.duration : 0;
        return Math.round(d || ctx.duration || 0);
    }

    // ---------- Telemetri ----------

    function report(state, opts) {
        if (!ctx || !ctx.songId || !ctx.url) { return; }
        var body = JSON.stringify({
            userId: ctx.userId || null,
            songId: ctx.songId,
            state: state,
            positionSeconds: Math.round(el.audio ? el.audio.currentTime : 0),
            durationSeconds: durationSeconds()
        });
        var headers = { "Content-Type": "application/json" };
        if (ctx.token) { headers["Authorization"] = "Bearer " + ctx.token; }

        try {
            // keepalive: sayfa kapanırken/geçilirken bile isteğin tamamlanmasını sağlar
            window.fetch(ctx.url, {
                method: "POST",
                headers: headers,
                body: body,
                keepalive: !!(opts && opts.keepalive),
                credentials: "same-origin"
            }).catch(function () { /* telemetri hatası dinlemeyi asla bölmez */ });
        } catch (e) { /* eski tarayıcı */ }
    }

    function reportTerminal(state) {
        if (terminalReported) { return; }
        terminalReported = true;
        stopHeartbeat();
        report(state, { keepalive: true });
    }

    function startHeartbeat() {
        stopHeartbeat();
        heartbeatTimer = window.setInterval(function () { report("PROGRESS"); }, HEARTBEAT_INTERVAL_MS);
    }

    function stopHeartbeat() {
        if (heartbeatTimer) { window.clearInterval(heartbeatTimer); heartbeatTimer = null; }
    }

    // ---------- UI senkronizasyonu ----------

    function renderPlayState() {
        var icon = el.toggle && el.toggle.querySelector("i");
        if (!icon) { return; }
        var playing = !el.audio.paused;
        icon.className = playing ? "fa-solid fa-pause" : "fa-solid fa-play";
        el.root.classList.toggle("is-playing", playing);
    }

    function renderVolume() {
        var icon = el.mute && el.mute.querySelector("i");
        if (!icon) { return; }
        var v = el.audio.muted ? 0 : el.audio.volume;
        icon.className = "fa-solid " + (v === 0 ? "fa-volume-xmark" : v < 0.5 ? "fa-volume-low" : "fa-volume-high");
        el.volume.value = String(v);
        el.volume.style.setProperty("--fill", (v * 100) + "%");
    }

    function renderProgress() {
        var d = el.audio.duration;
        var t = el.audio.currentTime;
        el.current.textContent = formatTime(t);
        el.total.textContent = formatTime(isFinite(d) ? d : ctx.duration);
        if (!isSeeking && isFinite(d) && d > 0) {
            var pct = (t / d) * 100;
            el.seek.value = String(pct);
            el.seek.style.setProperty("--fill", pct + "%");
        }
    }

    // ---------- Olay işleyicileri ----------

    function onPlay() {
        renderPlayState();
        startHeartbeat();
        report(el.audio.currentTime < 1 ? "STARTED" : "PROGRESS");
    }

    function onPause() {
        renderPlayState();
        stopHeartbeat();
        if (!el.audio.ended) { report("PAUSED"); }
        saveState({ songId: ctx.songId, position: el.audio.currentTime });
    }

    function onEnded() {
        renderPlayState();
        reportTerminal("COMPLETED");
        saveState({ songId: null, position: 0 });
        var autoNext = document.querySelector("[id$='autoNextBtn']");
        if (autoNext) { autoNext.click(); }
    }

    function onTimeUpdate() {
        renderProgress();
        // Her ~5 saniyede bir konumu kaydet (sayfa geçişinde kaldığı yerden devam için)
        if (Math.floor(el.audio.currentTime) % 5 === 0) {
            saveState({ songId: ctx.songId, position: el.audio.currentTime });
        }
    }

    function onSeekInput() {
        isSeeking = true;
        var d = el.audio.duration;
        el.seek.style.setProperty("--fill", el.seek.value + "%");
        if (isFinite(d)) { el.current.textContent = formatTime((el.seek.value / 100) * d); }
    }

    function onSeekChange() {
        var d = el.audio.duration;
        if (isFinite(d)) { el.audio.currentTime = (el.seek.value / 100) * d; }
        isSeeking = false;
    }

    function onVolumeInput() {
        el.audio.volume = parseFloat(el.volume.value);
        el.audio.muted = el.audio.volume === 0;
        saveState({ volume: el.audio.volume, muted: el.audio.muted });
        renderVolume();
    }

    function toggle() {
        if (!el.audio) { return; }
        if (el.audio.paused) {
            var p = el.audio.play();
            if (p && p.catch) { p.catch(function () { renderPlayState(); }); } // autoplay engeli
        } else {
            el.audio.pause();
        }
    }

    function toggleMute() {
        el.audio.muted = !el.audio.muted;
        saveState({ muted: el.audio.muted });
        renderVolume();
    }

    function onKeyDown(e) {
        var tag = (e.target && e.target.tagName || "").toLowerCase();
        if (tag === "input" || tag === "textarea" || tag === "select" || e.target.isContentEditable) { return; }
        if (!el.audio) { return; }
        if (e.code === "Space") { e.preventDefault(); toggle(); }
        else if (e.key === "m" || e.key === "M") { toggleMute(); }
        else if (e.key === "ArrowRight" && e.shiftKey) { el.audio.currentTime = Math.min(el.audio.currentTime + 10, el.audio.duration || Infinity); }
        else if (e.key === "ArrowLeft" && e.shiftKey) { el.audio.currentTime = Math.max(el.audio.currentTime - 10, 0); }
    }

    function onPageHide() {
        if (!el.audio || !ctx) { return; }
        saveState({ songId: ctx.songId, position: el.audio.currentTime });
        if (!el.audio.paused) { report("PAUSED", { keepalive: true }); }
    }

    // ---------- Yaşam döngüsü ----------

    function unmount() {
        stopHeartbeat();
        if (el.audio) {
            el.audio.removeEventListener("play", onPlay);
            el.audio.removeEventListener("pause", onPause);
            el.audio.removeEventListener("ended", onEnded);
            el.audio.removeEventListener("timeupdate", onTimeUpdate);
            el.audio.removeEventListener("loadedmetadata", renderProgress);
        }
        el = {};
        ctx = null;
    }

    function mount() {
        var root = document.getElementById("rzPlayer");
        if (!root) { unmount(); return; }

        // Aynı DOM düğümüne tekrar bağlanma
        if (el.root === root) { return; }
        unmount();

        el.root = root;
        el.audio = root.querySelector("#rzAudio");
        el.toggle = root.querySelector("[data-action='toggle']");
        el.mute = root.querySelector("[data-action='mute']");
        el.seek = root.querySelector("[data-role='seek']");
        el.volume = root.querySelector("[data-role='volume']");
        el.current = root.querySelector("[data-role='current']");
        el.total = root.querySelector("[data-role='total']");

        ctx = {
            songId: root.dataset.songId,
            userId: root.dataset.userId || null,
            token: root.dataset.token || null,
            url: root.dataset.heartbeatUrl,
            duration: parseInt(root.dataset.duration, 10) || 0
        };
        terminalReported = false;

        // Ses tercihlerini ve (aynı şarkıysa) kalınan konumu geri yükle
        var saved = loadState();
        el.audio.volume = typeof saved.volume === "number" ? saved.volume : 0.8;
        el.audio.muted = !!saved.muted;
        if (saved.songId === ctx.songId && saved.position > 0) {
            el.audio.addEventListener("loadedmetadata", function restore() {
                el.audio.removeEventListener("loadedmetadata", restore);
                if (saved.position < el.audio.duration - 1) { el.audio.currentTime = saved.position; }
            });
        }
        saveState({ songId: ctx.songId });

        el.audio.addEventListener("play", onPlay);
        el.audio.addEventListener("pause", onPause);
        el.audio.addEventListener("ended", onEnded);
        el.audio.addEventListener("timeupdate", onTimeUpdate);
        el.audio.addEventListener("loadedmetadata", renderProgress);

        el.toggle.addEventListener("click", toggle);
        el.mute.addEventListener("click", toggleMute);
        el.seek.addEventListener("input", onSeekInput);
        el.seek.addEventListener("change", onSeekChange);
        el.volume.addEventListener("input", onVolumeInput);

        if (!listenersBound) {
            document.addEventListener("keydown", onKeyDown);
            window.addEventListener("pagehide", onPageHide);
            listenersBound = true;
        }

        renderVolume();
        renderProgress();
        renderPlayState();

        // Tarayıcı izin verirse otomatik başlat; vermezse kullanıcı oynat'a basar
        var p = el.audio.play();
        if (p && p.catch) { p.catch(function () { renderPlayState(); }); }
    }

    window.RezonansPlayer = {
        mount: mount,
        toggle: toggle,
        /** Kullanıcı şarkı bitmeden ileri/geri geçtiğinde çağrılır. */
        reportSkip: function () {
            if (el.audio && !el.audio.ended) { reportTerminal("SKIPPED"); }
            saveState({ songId: null, position: 0 });
        }
    };
})(window, document);
