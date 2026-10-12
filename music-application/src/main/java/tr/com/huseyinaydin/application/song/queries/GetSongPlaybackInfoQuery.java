package tr.com.huseyinaydin.application.song.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IPlaybackService;
import tr.com.huseyinaydin.application.ports.ISongSearchService;
import tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Şarkı çalma yetkisini, ses dosya URL'sini ve meta verilerini dönen CQRS sorgusu.
 */
public record GetSongPlaybackInfoQuery(
        Long songId,
        String userEmail,
        String userTier
) implements IQuery<Map<String, Object>> {
    private static final Logger log = LoggerFactory.getLogger(GetSongPlaybackInfoQuery.class);

    @Component
    public static class Handler implements IQueryHandler<GetSongPlaybackInfoQuery, Map<String, Object>> {
        private final IPlaybackService playbackService;
        private final ISongSearchService songSearchService;

        public Handler(@Autowired(required = false) IPlaybackService playbackService,
                       @Autowired(required = false) ISongSearchService songSearchService) {
            this.playbackService = playbackService;
            this.songSearchService = songSearchService;
        }

        @Override
        public Map<String, Object> handle(GetSongPlaybackInfoQuery query) {
            log.info("Executing GetSongPlaybackInfoQuery for songId: {}, tier: {}", query.songId(), query.userTier());

            if (playbackService != null) {
                if (!playbackService.hasAccess(query.songId(), query.userTier())) {
                    throw new AuthorizationException(
                            "playSong",
                            "Bu şarkı için en az " + playbackService.getRequiredTierLevel(query.songId()) + " paketine sahip olmanız gerekiyor.");
                }
            }

            String audioUrl = (playbackService != null) ? playbackService.getAudioUrl(query.songId()) : "";
            if (playbackService != null && query.userEmail() != null) {
                playbackService.recordListenEvent(query.userEmail(), query.songId());
            }

            Map<String, Object> song = (songSearchService != null) ? songSearchService.getSongById(query.songId()) : null;
            String title = (song != null && song.get("title") != null) ? String.valueOf(song.get("title")) : ("Song " + query.songId());
            String artist = (song != null && song.get("artist") != null) ? String.valueOf(song.get("artist")) : "";
            String cover = (song != null && song.get("cover") != null) ? String.valueOf(song.get("cover")) : "";

            Map<String, Object> response = new HashMap<>();
            response.put("songId", query.songId());
            response.put("audioUrl", audioUrl);
            response.put("title", title);
            response.put("artist", artist);
            response.put("cover", cover);
            response.put("playbackUrl", audioUrl);
            return response;
        }
    }
}
