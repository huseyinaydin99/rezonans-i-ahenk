package tr.com.huseyinaydin.domain.config;

/**
 * Global Müzik Akış (Streaming) konfigürasyonlarını yöneten SAF (Pure) Java Singleton sınıfı.
 * Hiçbir framework bağımlılığı içermez.
 */
public class StreamingConfigManager {

    // Sisteme özel katı kurallar
    private int defaultBitrate;
    private int maxConcurrentStreams;
    private String globalCdnUrl;

    // 1. Kurucu metodu gizle (private) ki dışarıdan new ile üretilemesin
    private StreamingConfigManager() {
        System.out.println("[SINGLETON INIT] Global Streaming Konfigürasyon Yöneticisi belleğe yükleniyor...");
        // Varsayılan değerler atanıyor (Örn: Veritabanından veya ortam değişkenlerinden de okunabilir)
        this.defaultBitrate = 128;
        this.maxConcurrentStreams = 10000;
        this.globalCdnUrl = "https://cdn.rezonans-ahenk.com/audio/";
    }

    // 2. Bill Pugh Singleton Tasarımı (Thread-Safe & High Performance)
    // Bu iç sınıf, getInstance() çağrılana kadar belleğe yüklenmez (Lazy Initialization).
    private static class InstanceHolder {
        private static final StreamingConfigManager INSTANCE = new StreamingConfigManager();
    }

    // 3. Tekil erişim noktası
    public static StreamingConfigManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    // Getters and Setters
    public int getDefaultBitrate() { return defaultBitrate; }
    public void setDefaultBitrate(int defaultBitrate) { this.defaultBitrate = defaultBitrate; }
    
    public int getMaxConcurrentStreams() { return maxConcurrentStreams; }
    public void setMaxConcurrentStreams(int maxConcurrentStreams) { this.maxConcurrentStreams = maxConcurrentStreams; }
    
    public String getGlobalCdnUrl() { return globalCdnUrl; }
    public void setGlobalCdnUrl(String globalCdnUrl) { this.globalCdnUrl = globalCdnUrl; }
}
