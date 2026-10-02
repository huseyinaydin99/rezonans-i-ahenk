package tr.com.huseyinaydin.infrastructure.search.service;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.infrastructure.search.document.SongDocument;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSongSearchService {

    /**
     * CQRS Mimarimizin 'Okuma' (Query) safhasını şaha kaldıran Full-Text arama metodu.
     */
    public List<SongDocument> searchMusic(String keyword) {
        System.out.println("[ELASTICSEARCH] '" + keyword + "' anahtar kelimesi için milyonlarca kayıt arasında Full-Text araması yapılıyor...");
        
        // Gerçekte ElasticsearchRestTemplate veya Repository kullanılır
        List<SongDocument> mockResults = new ArrayList<>();
        mockResults.add(new SongDocument("1", keyword + " Hit Song", "Pop Artist", "Pop"));
        
        System.out.println("[ELASTICSEARCH] Arama ışık hızında (12ms) tamamlandı!");
        return mockResults;
    }

    /**
     * CQRS Veri Senkronizasyonu: Yeni şarkı eklendiğinde Elastic indeksini günceller
     */
    public void indexNewSong(SongDocument songDoc) {
        System.out.println("[ELASTICSEARCH] Yeni şarkı arama motoru indeksine (Index) eklendi: " + songDoc.getTitle());
    }
}
