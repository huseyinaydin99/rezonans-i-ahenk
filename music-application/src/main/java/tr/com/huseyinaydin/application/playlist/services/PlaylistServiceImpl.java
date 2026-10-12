package tr.com.huseyinaydin.application.playlist.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.application.playlist.dtos.CreatePlaylistDto;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.domain.playlist.Playlist;
import tr.com.huseyinaydin.domain.repositories.IPlaylistRepository;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlaylistServiceImpl implements IPlaylistService {
    private static final Logger log = LoggerFactory.getLogger(PlaylistServiceImpl.class);

    private final IPlaylistRepository playlistRepository;
    private final ISongSearchService songSearchService;

    private final Map<String, List<PlaylistDetailDto>> userPlaylistsStore = new ConcurrentHashMap<>();

    public PlaylistServiceImpl(@Autowired(required = false) IPlaylistRepository playlistRepository,
                               @Autowired(required = false) ISongSearchService songSearchService) {
        this.playlistRepository = playlistRepository;
        this.songSearchService = songSearchService;
    }

    private String normalizeKey(String userId) {
        return (userId != null) ? userId.trim().toLowerCase(Locale.ROOT) : "";
    }

    @Override
    @Transactional
    public void createPlaylist(CreatePlaylistDto dto) {
        if (dto == null || dto.getName() == null || dto.getName().trim().isEmpty()) {
            return;
        }
        String key = normalizeKey(dto.getUserId());
        String name = dto.getName().trim();
        String description = dto.getDescription() != null ? dto.getDescription().trim() : "";
        String playlistId = UUID.randomUUID().toString();

        log.info("[PLAYLIST SERVİSİ] Kullanıcı ({}) için yeni çalma listesi oluşturuluyor: {}", key, name);

        PlaylistDetailDto newDetail = new PlaylistDetailDto(
            playlistId,
            name,
            description,
            new ArrayList<>()
        );

        userPlaylistsStore.computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>())).add(0, newDetail);

        if (playlistRepository != null) {
            try {
                UUID userUuid;
                try {
                    userUuid = UUID.fromString(dto.getUserId());
                } catch (Exception ex) {
                    userUuid = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
                }
                Playlist domainPlaylist = Playlist.builder()
                    .id(UUID.fromString(playlistId))
                    .name(name)
                    .description(description)
                    .ownerId(userUuid)
                    .build();
                playlistRepository.save(domainPlaylist);
            } catch (Exception e) {
                log.warn("[PLAYLIST SERVİSİ] Repository kaydı uyarısı (bellekte korundu): {}", e.getMessage());
            }
        }
        log.info("-> Çalma listesi başarıyla kaydedildi. ID: {}", playlistId);
    }

    @Override
    @Transactional
    public void addSongToPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null) return;
        log.info("[PLAYLIST SERVİSİ] Şarkı ({}), Çalma Listesine ({}) ekleniyor...", songId, playlistId);

        String songTitle = "Şarkı #" + songId;
        if (songSearchService != null) {
            try {
                Long numericId = Long.parseLong(songId);
                List<Map<String, Object>> songs = songSearchService.getSongsByIds(List.of(numericId));
                if (songs != null && !songs.isEmpty()) {
                    songTitle = (String) songs.get(0).getOrDefault("title", songTitle);
                }
            } catch (Exception ignored) {}
        }

        boolean found = false;
        for (List<PlaylistDetailDto> list : userPlaylistsStore.values()) {
            for (PlaylistDetailDto p : list) {
                if (p.getId().equals(playlistId)) {
                    if (p.getSongTitles() != null && !p.getSongTitles().contains(songTitle)) {
                        p.getSongTitles().add(songTitle);
                    }
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null) return;
        log.info("[PLAYLIST SERVİSİ] Şarkı ({}), Çalma Listesinden ({}) çıkarılıyor...", songId, playlistId);
        for (List<PlaylistDetailDto> list : userPlaylistsStore.values()) {
            for (PlaylistDetailDto p : list) {
                if (p.getId().equals(playlistId) && p.getSongTitles() != null) {
                    p.getSongTitles().removeIf(t -> t.contains(songId));
                }
            }
        }
    }

    @Override
    public List<PlaylistDetailDto> getUserPlaylists(String userId) {
        String key = normalizeKey(userId);
        List<PlaylistDetailDto> list = userPlaylistsStore.computeIfAbsent(key, k -> {
            List<PlaylistDetailDto> initial = Collections.synchronizedList(new ArrayList<>());
            initial.add(new PlaylistDetailDto(
                UUID.randomUUID().toString(),
                "Yolculuk Şarkıları",
                "Uzun yolda dinlenecek en iyi parçalar.",
                new ArrayList<>(List.of("The Weeknd - Save Your Tears", "Coldplay - Yellow"))
            ));
            initial.add(new PlaylistDetailDto(
                UUID.randomUUID().toString(),
                "Spor Salonu Motivasyon",
                "Ağırlık kaldırırken dinlenecek enerjik liste.",
                new ArrayList<>(List.of("Eminem - Lose Yourself"))
            ));

            if (playlistRepository != null) {
                try {
                    UUID userUuid;
                    try {
                        userUuid = UUID.fromString(userId);
                    } catch (Exception ex) {
                        userUuid = UUID.nameUUIDFromBytes(key.getBytes(StandardCharsets.UTF_8));
                    }
                    List<Playlist> domainList = playlistRepository.findByUserId(userUuid);
                    if (domainList != null) {
                        for (Playlist dp : domainList) {
                            initial.add(0, new PlaylistDetailDto(
                                dp.getId().toString(),
                                dp.getName(),
                                dp.getDescription() != null ? dp.getDescription() : "",
                                new ArrayList<>()
                            ));
                        }
                    }
                } catch (Exception e) {
                    log.warn("[PLAYLIST SERVİSİ] Repository listeleme uyarısı: {}", e.getMessage());
                }
            }
            return initial;
        });

        return new ArrayList<>(list);
    }
}
