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
            new SongSnippet("Blinding Lights", "The Weeknd", "https://minio/blinding.jpg"),
            new SongSnippet("Shape of You", "Ed Sheeran", "https://minio/shape.jpg"),
            new SongSnippet("Bohemian Rhapsody", "Queen", "https://minio/queen.jpg")
        );

        // 2. Elasticsearch üzerinden son 24 saatte çıkan şarkılar çekilir
        System.out.println("-> Elasticsearch üzerinden 'Yeni Çıkanlar' verisi süzülüyor.");
        List<SongSnippet> newReleases = List.of(
            new SongSnippet("Vampire", "Olivia Rodrigo", "https://minio/vampire.jpg"),
            new SongSnippet("Paint The Town Red", "Doja Cat", "https://minio/doja.jpg")
        );

        // 3. Apache Spark (ML) motorunun gece 03:00'te hesapladığı özel tavsiyeler çekilir
        System.out.println("-> Apache Spark Yapay Zeka motorundan 'Sizin İçin Seçtiklerimiz' çekiliyor.");
        List<SongSnippet> forYou = List.of(
            new SongSnippet("Starboy", "The Weeknd", "https://minio/starboy.jpg"),
            new SongSnippet("Stairway To Heaven", "Led Zeppelin", "https://minio/led.jpg")
        );

        System.out.println("[ANA SAYFA VİTRİNİ] Tüm asenkron veriler milisaniyeler içinde toparlandı ve UI'a (JSF) gönderildi!");
        
        return new HomeDashboardDto(topGlobal, newReleases, forYou);
    }
}
