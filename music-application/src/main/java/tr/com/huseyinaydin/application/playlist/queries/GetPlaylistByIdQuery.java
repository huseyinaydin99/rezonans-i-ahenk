package tr.com.huseyinaydin.application.playlist.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

/**
 * Belirli bir çalma listesinin detaylarını getiren CQRS sorgusu.
 */
public record GetPlaylistByIdQuery(String playlistId) implements IQuery<PlaylistDetailDto> {
    private static final Logger log = LoggerFactory.getLogger(GetPlaylistByIdQuery.class);

    @Component
    public static class Handler implements IQueryHandler<GetPlaylistByIdQuery, PlaylistDetailDto> {
        private final IPlaylistService playlistService;

        public Handler(@Autowired(required = false) IPlaylistService playlistService) {
            this.playlistService = playlistService;
        }

        @Override
        public PlaylistDetailDto handle(GetPlaylistByIdQuery query) {
            log.info("Executing GetPlaylistByIdQuery for playlistId: {}", query.playlistId());
            if (playlistService != null && query.playlistId() != null) {
                return playlistService.getPlaylistById(query.playlistId());
            }
            return null;
        }
    }
}
