// WebSocket (STOMP + SockJS) Entegrasyonu
var stompClient = null;

function connectToMusicStream() {
    var socket = new SockJS('/ws-music');
    stompClient = Stomp.over(socket);
    
    stompClient.connect({}, function(frame) {
        console.log('Müzik Platformu Canlı Akışına Bağlanıldı: ' + frame);
        
        // Sunucudan (Kafka -> Bridge -> WS) gelen canlı bildirimleri dinle
        stompClient.subscribe('/topic/notifications', function(notification) {
            showLiveToast(notification.body);
        });
    });
}

function showLiveToast(message) {
    // Ekranda kullanıcıya sayfayı yenilemeden bildirim (Toast) gösterir
    console.log("🔥 CANLI BİLDİRİM: " + message);
    
    // Basit DOM manipülasyonu
    var toast = document.createElement("div");
    toast.className = "alert alert-success position-fixed bottom-0 end-0 m-3";
    toast.style.zIndex = "9999";
    toast.innerHTML = "<strong><i class='fa-solid fa-bell'></i> Yeni Olay!</strong> " + message;
    
    document.body.appendChild(toast);
    
    // 5 saniye sonra bildirimi kaybet
    setTimeout(function() {
        toast.remove();
    }, 5000);
}

// Sayfa yüklendiğinde bağlantıyı aç
document.addEventListener("DOMContentLoaded", function() {
    connectToMusicStream();
});
