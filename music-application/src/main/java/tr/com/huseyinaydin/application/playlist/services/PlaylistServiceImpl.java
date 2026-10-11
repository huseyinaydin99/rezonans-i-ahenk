package tr.com.huseyinaydin.application.playlist.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PlaylistServiceImpl implements IPlaylistService {
    private static final Logger log = LoggerFactory.getLogger(PlaylistServiceImpl.class);


    @Override
    @Transactional
    public void createPlaylist(CreatePlaylistDto dto) {
        log.info("[PLAYLIST SERVİSİ] Kullanıcı (" + dto.getUserId() + ") için yeni çalma listesi oluşturuluyor: " + dto.getName());
        // Gerçekte Playlist entity'si yaratılıp repository.save(playlist) yapılır.
        log.info("-> Çalma listesi veritabanına başarıyla kaydedildi.");
    }

    @Override
    @Transactional
    public void addSongToPlaylist(String playlistId, String songId) {
        log.info("[PLAYLIST SERVİSİ] Şarkı (" + songId + "), Çalma Listesine (" + playlistId + ") ekleniyor...");
        // Gerçekte ManyToMany ilişki (PlaylistSong) tablosuna insert atılır.
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(String playlistId, String songId) {
        log.info("[PLAYLIST SERVİSİ] Şarkı (" + songId + "), Çalma Listesinden (" + playlistId + ") çıkarılıyor...");
    }

    @Override
    public List<PlaylistDetailDto> getUserPlaylists(String userId) {
        // Simülasyon
        List<PlaylistDetailDto> mockList = new ArrayList<>();
        mockList.add(new PlaylistDetailDto(
            UUID.randomUUID().toString(), 
            "Yolculuk Şarkıları", 
            "Uzun yolda dinlenecek en iyi parçalar.", 
            List.of("The Weeknd - Save Your Tears", "Coldplay - Yellow")
        ));
        mockList.add(new PlaylistDetailDto(
            UUID.randomUUID().toString(), 
            "Spor Salonu Motivasyon", 
            "Ağırlık kaldırırken dinlenecek enerjik liste.", 
            List.of("Eminem - Lose Yourself")
        ));
        return mockList;
    }
}
