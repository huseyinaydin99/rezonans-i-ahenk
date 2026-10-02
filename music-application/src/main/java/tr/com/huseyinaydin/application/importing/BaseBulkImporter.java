package tr.com.huseyinaydin.application.importing;

import tr.com.huseyinaydin.domain.song.Song;
import java.util.List;

/**
 * Şablon Metot (Template Method) tasarım deseninin ana iskelet sınıfı.
 */
public abstract class BaseBulkImporter {

    /**
     * NİHAİ (FINAL) ŞABLON METOT:
     * Algoritmanın ana akış iskeletini belirler. Alt sınıflar bu akışı asla değiştiremez (override edemez).
     */
    public final void importData(String filePath) {
        System.out.println("---- Toplu Veri Aktarımı Başlıyor: " + filePath + " ----");
        
        // 1. Adım: Dosyayı belleğe oku (Ortak mantık)
        String rawContent = readFile(filePath);
        
        // 2. Adım: Veriyi formatına göre ayrıştır (Subclass - Alt sınıf bunu implement etmek ZORUNDA)
        List<Song> parsedSongs = parseData(rawContent);
        
        // 3. Adım: Ayrıştırılan veriyi doğrula (Ortak mantık)
        validateData(parsedSongs);
        
        // 4. Adım: Veritabanına kaydet (Ortak mantık)
        saveToDatabase(parsedSongs);
        
        System.out.println("---- Toplu Veri Aktarımı Tamamlandı ----\n");
    }

    // --- Ortak (Common) Adımlar ---
    
    protected String readFile(String filePath) {
        System.out.println("[ADIM 1] Dosya sistemden okunuyor... (" + filePath + ")");
        // IO işlemleri simülasyonu
        return "mock_raw_data"; 
    }
    
    protected void validateData(List<Song> songs) {
        System.out.println("[ADIM 3] Ayrıştırılan veriler (Business Rules) doğrulanıyor...");
        if (songs == null) {
            throw new IllegalArgumentException("Veri ayrıştırma hatası! Kayıt bulunamadı.");
        }
    }
    
    protected void saveToDatabase(List<Song> songs) {
        System.out.println("[ADIM 4] Doğrulanan " + songs.size() + " kayıt veritabanına toplu (Batch) olarak kaydediliyor...");
    }

    // --- Alt Sınıfa (Subclass) Bırakılan Özel Adım ---
    
    /**
     * Her dosya tipinin ayrıştırma mantığı farklı olduğu için soyut (abstract) bırakılmıştır.
     * CsvImporter, JsonImporter gibi alt sınıflar kendi formatına göre burayı doldurur.
     */
    protected abstract List<Song> parseData(String rawContent);
}
