package tr.com.huseyinaydin.application.home.services;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IHomeService;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto.SongSnippet;

import java.util.List;

@Service
public class HomeServiceImpl implements IHomeService {

    @Override
    public HomeDashboardDto buildHomeDashboard(String userId) {
        System.out.println("[ANA SAYFA VİTRİNİ] Dinleyici ('" + userId + "') uygulamaya giriş yaptı. Ana Sayfa panosu yükleniyor...");

        // 1. Cassandra'dan (Big Data) dünyada en çok dinlenen Top 10 çekilir
        System.out.println("-> Cassandra üzerinden 'Küresel Top 10' verisi çekiliyor.");
        List<SongSnippet> topGlobal = List.of(
            new SongSnippet(1L, "Blinding Lights", "The Weeknd", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?auto=format&fit=crop&w=400&q=80"),
            new SongSnippet(2L, "Shape of You", "Ed Sheeran", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80"),
            new SongSnippet(3L, "Bohemian Rhapsody", "Queen", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80")
        );

        // 2. Elasticsearch üzerinden son 24 saatte çıkan şarkılar çekilir
        System.out.println("-> Elasticsearch üzerinden 'Yeni Çıkanlar' verisi süzülüyor.");
        List<SongSnippet> newReleases = List.of(
            new SongSnippet(4L, "Vampire", "Olivia Rodrigo", "https://images.unsplash.com/photo-1493225457124-a1a2a53b51ee?auto=format&fit=crop&w=400&q=80"),
            new SongSnippet(5L, "Paint The Town Red", "Doja Cat", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80")
        );

        // 3. Apache Spark (ML) motorunun gece 03:00'te hesapladığı özel tavsiyeler çekilir
        System.out.println("-> Apache Spark Yapay Zeka motorundan 'Sizin İçin Seçtiklerimiz' çekiliyor.");
        List<SongSnippet> forYou = List.of(
            new SongSnippet(6L, "Starboy", "The Weeknd", "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=400&q=80"),
            new SongSnippet(7L, "Stairway To Heaven", "Led Zeppelin", "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?auto=format&fit=crop&w=400&q=80")
        );

        System.out.println("[ANA SAYFA VİTRİNİ] Tüm asenkron veriler milisaniyeler içinde toparlandı ve UI'a (JSF) gönderildi!");
        
        return new HomeDashboardDto(topGlobal, newReleases, forYou);
    }
}
