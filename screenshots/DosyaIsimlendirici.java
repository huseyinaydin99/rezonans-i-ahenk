import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.UUID;

public class DosyaIsimlendirici {

    public static void main(String[] args) {
        // Programın çalıştığı mevcut klasörü al
        File klasor = new File(System.getProperty("user.dir"));
        File[] dosyalar = klasor.listFiles(File::isFile);

        if (dosyalar == null || dosyalar.length == 0) {
            System.out.println("Klasörde isimlendirilecek dosya bulunamadı.");
            return;
        }

        // Alfabetik sıraya göre diz (sıralamanın tutarlı olması için)
        Arrays.sort(dosyalar, Comparator.comparing(File::getName));

        // 1. AŞAMA: Klasörde halihazırda "1.jpg", "2.jpg" gibi dosyalar varsa
        // çakışma yaşanmaması için önce geçici benzersiz isimler veriyoruz.
        for (File dosya : dosyalar) {
            String ad = dosya.getName();
            // Kodun kendisini yeniden adlandırmasını engelle
            if (ad.equals("DosyaIsimlendirici.java") || ad.equals("DosyaIsimlendirici.class")) {
                continue;
            }
            try {
                String uzanti = uzantiyiAl(ad);
                Path geciciYol = dosya.toPath().resolveSibling("gecici_" + UUID.randomUUID() + uzanti);
                Files.move(dosya.toPath(), geciciYol, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException e) {
                System.err.println("Geçici isimlendirme hatası (" + ad + "): " + e.getMessage());
            }
        }

        // Geçici isimlendirilmiş dosyaları tekrar listele ve sırala
        File[] geciciDosyalar = klasor.listFiles(f -> f.isFile() && f.getName().startsWith("gecici_"));
        if (geciciDosyalar == null) return;
        Arrays.sort(geciciDosyalar, Comparator.comparing(File::getName));

        // 2. AŞAMA: Sırasıyla 1, 2, 3, 4, 5... olarak yeniden adlandır
        int sayac = 1;
        for (File dosya : geciciDosyalar) {
            String uzanti = uzantiyiAl(dosya.getName());
            String yeniAd = sayac + uzanti; // Uzantısız sadece "1, 2, 3" isterseniz "+ uzanti" kısmını silin
            Path yeniYol = dosya.toPath().resolveSibling(yeniAd);

            try {
                Files.move(dosya.toPath(), yeniYol);
                System.out.println("Yeni isim: " + yeniAd);
                sayac++;
            } catch (IOException e) {
                System.err.println("İsim değiştirilemedi: " + e.getMessage());
            }
        }

        System.out.println("İşlem tamamlandı! Toplam " + (sayac - 1) + " dosya isimlendirildi.");
    }

    // Dosya uzantısını (.png, .pdf, .txt vb.) koruyan yardımcı metot
    private static String uzantiyiAl(String dosyaAdi) {
        int sonNokta = dosyaAdi.lastIndexOf('.');
        if (sonNokta > 0 && sonNokta < dosyaAdi.length() - 1) {
            return dosyaAdi.substring(sonNokta);
        }
        return "";
    }
}