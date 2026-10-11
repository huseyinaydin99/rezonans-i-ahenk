package tr.com.huseyinaydin.infrastructure.search.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.infrastructure.ElasticSong;
import tr.com.huseyinaydin.infrastructure.IElasticSongRepository;
import tr.com.huseyinaydin.infrastructure.search.document.SongDocument;

import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticSongSearchService {
    private static final Logger log = LoggerFactory.getLogger(ElasticSongSearchService.class);

    private final IElasticSongRepository elasticSongRepository;

    public ElasticSongSearchService(@Autowired(required = false) IElasticSongRepository elasticSongRepository) {
        this.elasticSongRepository = elasticSongRepository;
    }

    /**
     * CQRS Mimarimizin 'Okuma' (Query) safhasını gerçekleştiren Full-Text arama metodu.
     */
    public List<SongDocument> searchMusic(String keyword) {
        log.info("[ELASTICSEARCH] '{}' anahtar kelimesi için Full-Text araması yapılıyor...", keyword);
        
        List<SongDocument> results = new ArrayList<>();
        if (elasticSongRepository != null && keyword != null && !keyword.isBlank()) {
            try {
                List<ElasticSong> elasticSongs = elasticSongRepository.findByTitleContainingOrArtistContaining(keyword, keyword);
                for (ElasticSong es : elasticSongs) {
                    results.add(new SongDocument(es.getId(), es.getTitle(), es.getArtist(), es.getTier()));
                }
            } catch (Exception e) {
                log.warn("[ELASTICSEARCH] Full-Text arama esnasında uyarı/hata: {}", e.getMessage());
            }
        }
        return results;
    }

    /**
     * CQRS Veri Senkronizasyonu: Yeni şarkı eklendiğinde Elastic indeksini günceller
     */
    public void indexNewSong(SongDocument songDoc) {
        log.info("[ELASTICSEARCH] Yeni şarkı arama motoru indeksine eklendi: {}", songDoc != null ? songDoc.getTitle() : "");
        if (elasticSongRepository != null && songDoc != null) {
            try {
                ElasticSong es = new ElasticSong();
                es.setId(songDoc.getId());
                es.setTitle(songDoc.getTitle());
                es.setArtist(songDoc.getArtistName());
                es.setTier("Basic");
                elasticSongRepository.save(es);
            } catch (Exception e) {
                log.error("[ELASTICSEARCH] Şarkı indekslenirken hata: {}", e.getMessage());
            }
        }
    }
}
