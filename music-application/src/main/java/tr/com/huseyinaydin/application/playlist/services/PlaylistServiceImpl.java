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

    private void hydratePlaylist(PlaylistDetailDto p) {
        if (p == null) return;
        if (p.getSongTitles() == null) p.setSongTitles(Collections.synchronizedList(new ArrayList<>()));
        if (p.getSongIds() == null) p.setSongIds(Collections.synchronizedList(new ArrayList<>()));
        if (p.getSongs() == null) p.setSongs(Collections.synchronizedList(new ArrayList<>()));

        if (p.getSongs().isEmpty() && !p.getSongIds().isEmpty() && songSearchService != null) {
            try {
                List<Map<String, Object>> fetched = songSearchService.getSongsByIds(p.getSongIds());
                if (fetched != null) {
                    p.getSongs().addAll(fetched);
                }
            } catch (Exception ignored) {}
        }

        // Eğer songs hala boşsa ancak songTitles varsa, katalogdan eşleştirmeyi dene veya mock şarkılar üret
        if (p.getSongs().isEmpty() && !p.getSongTitles().isEmpty() && songSearchService != null) {
            for (int i = 0; i < p.getSongTitles().size(); i++) {
                String title = p.getSongTitles().get(i);
                long fallbackId = (i + 1);
                Map<String, Object> s = songSearchService.getSongById(fallbackId);
                if (s != null) {
                    p.getSongs().add(s);
                    if (!p.getSongIds().contains(fallbackId)) {
                        p.getSongIds().add(fallbackId);
                    }
                }
            }
        }
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

        // 3. Sorun Çözümü: Aynı isimde başka bir playlist oluşturulamaz (büyük/küçük harf duyarsız)
        List<PlaylistDetailDto> existingPlaylists = getUserPlaylists(key);
        for (PlaylistDetailDto existing : existingPlaylists) {
            if (existing.getName() != null && existing.getName().trim().equalsIgnoreCase(name)) {
                log.warn("[PLAYLIST SERVİSİ] Kullanıcı ({}) için '{}' isimli bir çalma listesi zaten mevcut!", key, name);
                throw new IllegalArgumentException("'" + name + "' adında bir çalma listeniz zaten mevcut!");
            }
        }

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
    public boolean addSongToPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null || songId.isBlank()) return false;
        log.info("[PLAYLIST SERVİSİ] Şarkı ({}), Çalma Listesine ({}) ekleniyor...", songId, playlistId);

        Long numericId = null;
        try {
            numericId = Long.parseLong(songId.trim());
        } catch (Exception ignored) {}

        Map<String, Object> songData = null;
        if (numericId != null && songSearchService != null) {
            try {
                songData = songSearchService.getSongById(numericId);
            } catch (Exception ignored) {}
        }

        String songTitle = (songData != null && songData.get("title") != null)
                ? (String) songData.get("title")
                : "Şarkı #" + songId;

        boolean added = false;
        for (List<PlaylistDetailDto> list : userPlaylistsStore.values()) {
            for (PlaylistDetailDto p : list) {
                if (p.getId().equals(playlistId)) {
                    if (p.getSongTitles() == null) p.setSongTitles(Collections.synchronizedList(new ArrayList<>()));
                    if (p.getSongIds() == null) p.setSongIds(Collections.synchronizedList(new ArrayList<>()));
                    if (p.getSongs() == null) p.setSongs(Collections.synchronizedList(new ArrayList<>()));

                    // 1. Sorun Çözümü: Bir müzik daha önce eklenmişse asla tekrar eklenemez (Tekil Ekleme)
                    boolean alreadyExists = false;
                    if (numericId != null && p.getSongIds().contains(numericId)) {
                        alreadyExists = true;
                    }
                    if (!alreadyExists && p.getSongTitles().stream().anyMatch(t -> t != null && t.equalsIgnoreCase(songTitle))) {
                        alreadyExists = true;
                    }
                    if (!alreadyExists) {
                        final Long finalNumId = numericId;
                        alreadyExists = p.getSongs().stream().anyMatch(s -> {
                            Object idVal = s.get("id");
                            if (finalNumId != null && idVal != null && Objects.equals(String.valueOf(idVal), String.valueOf(finalNumId))) {
                                return true;
                            }
                            Object tVal = s.get("title");
                            return (tVal != null && String.valueOf(tVal).equalsIgnoreCase(songTitle));
                        });
                    }

                    if (alreadyExists) {
                        log.info("[PLAYLIST SERVİSİ] Şarkı ('{}') zaten '{}' listesinde mevcut! Tekrar eklenmedi.", songTitle, p.getName());
                        return false;
                    }

                    // Şarkıyı sadece bir defa ekle
                    p.getSongTitles().add(songTitle);
                    if (numericId != null) {
                        p.getSongIds().add(numericId);
                    }
                    if (songData != null) {
                        p.getSongs().add(songData);
                    } else {
                        Map<String, Object> fallback = new HashMap<>();
                        fallback.put("id", numericId != null ? numericId : songId);
                        fallback.put("title", songTitle);
                        fallback.put("artist", "Rezonans Sanatçısı");
                        fallback.put("cover", "https://images.unsplash.com/photo-1514525253161-7a46d19cd819?w=300");
                        fallback.put("duration", "3:30");
                        fallback.put("tier", "Basic");
                        p.getSongs().add(fallback);
                    }
                    added = true;
                    break;
                }
            }
            if (added) break;
        }
        return added;
    }

    @Override
    @Transactional
    public void removeSongFromPlaylist(String playlistId, String songId) {
        if (playlistId == null || songId == null) return;
        log.info("[PLAYLIST SERVİSİ] Şarkı ({}), Çalma Listesinden ({}) çıkarılıyor...", songId, playlistId);
        Long numericId = null;
        try { numericId = Long.parseLong(songId.trim()); } catch (Exception ignored) {}

        for (List<PlaylistDetailDto> list : userPlaylistsStore.values()) {
            for (PlaylistDetailDto p : list) {
                if (p.getId().equals(playlistId)) {
                    final Long finalNumId = numericId;
                    if (p.getSongIds() != null && numericId != null) {
                        p.getSongIds().remove(numericId);
                    }
                    if (p.getSongs() != null) {
                        p.getSongs().removeIf(s -> {
                            Object idVal = s.get("id");
                            if (finalNumId != null && idVal != null && Objects.equals(String.valueOf(idVal), String.valueOf(finalNumId))) return true;
                            if (idVal != null && Objects.equals(String.valueOf(idVal), songId)) return true;
                            return false;
                        });
                    }
                    if (p.getSongTitles() != null) {
                        p.getSongTitles().removeIf(t -> t.contains(songId));
                    }
                }
            }
        }
    }

    @Override
    public PlaylistDetailDto getPlaylistById(String playlistId) {
        if (playlistId == null) return null;
        for (List<PlaylistDetailDto> list : userPlaylistsStore.values()) {
            for (PlaylistDetailDto p : list) {
                if (p.getId().equals(playlistId)) {
                    hydratePlaylist(p);
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public List<PlaylistDetailDto> getUserPlaylists(String userId) {
        String key = normalizeKey(userId);
        List<PlaylistDetailDto> list = userPlaylistsStore.computeIfAbsent(key, k -> {
            List<PlaylistDetailDto> initial = Collections.synchronizedList(new ArrayList<>());
            
            PlaylistDetailDto p1 = new PlaylistDetailDto(
                UUID.randomUUID().toString(),
                "Yolculuk Şarkıları",
                "Uzun yolda dinlenecek en iyi parçalar.",
                new ArrayList<>(List.of("Blinding Lights", "Starboy")),
                new ArrayList<>(List.of(1L, 2L)),
                new ArrayList<>()
            );
            hydratePlaylist(p1);
            initial.add(p1);

            PlaylistDetailDto p2 = new PlaylistDetailDto(
                UUID.randomUUID().toString(),
                "Spor Salonu Motivasyon",
                "Ağırlık kaldırırken dinlenecek enerjik liste.",
                new ArrayList<>(List.of("Shape of You", "Bohemian Rhapsody")),
                new ArrayList<>(List.of(3L, 5L)),
                new ArrayList<>()
            );
            hydratePlaylist(p2);
            initial.add(p2);

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
                            PlaylistDetailDto customP = new PlaylistDetailDto(
                                dp.getId().toString(),
                                dp.getName(),
                                dp.getDescription() != null ? dp.getDescription() : "",
                                new ArrayList<>()
                            );
                            hydratePlaylist(customP);
                            initial.add(0, customP);
                        }
                    }
                } catch (Exception e) {
                    log.warn("[PLAYLIST SERVİSİ] Repository listeleme uyarısı: {}", e.getMessage());
                }
            }
            return initial;
        });

        for (PlaylistDetailDto p : list) {
            hydratePlaylist(p);
        }

        return new ArrayList<>(list);
    }

    @Override
    public void migrateUser(String oldUserId, String newUserId) {
        if (oldUserId == null || newUserId == null) return;
        String oldKey = normalizeKey(oldUserId);
        String newKey = normalizeKey(newUserId);
        if (oldKey.isEmpty() || newKey.isEmpty() || oldKey.equalsIgnoreCase(newKey)) return;

        List<PlaylistDetailDto> oldPlaylists = userPlaylistsStore.remove(oldKey);
        if (oldPlaylists != null && !oldPlaylists.isEmpty()) {
            userPlaylistsStore.compute(newKey, (k, existing) -> {
                if (existing == null) {
                    return Collections.synchronizedList(new ArrayList<>(oldPlaylists));
                } else {
                    existing.addAll(oldPlaylists);
                    return existing;
                }
            });
            log.info("[PLAYLIST SERVİSİ] Kullanıcı çalma listeleri aktarıldı: {} -> {} ({} liste)", oldKey, newKey, oldPlaylists.size());
        }
    }
}
