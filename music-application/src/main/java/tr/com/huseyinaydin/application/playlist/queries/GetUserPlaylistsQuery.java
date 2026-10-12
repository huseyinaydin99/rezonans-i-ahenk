package tr.com.huseyinaydin.application.playlist.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.playlist.dtos.PlaylistDetailDto;
import tr.com.huseyinaydin.application.ports.IPlaylistService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.Collections;
import java.util.List;

/**
 * Kullanıcıya ait tüm çalma listelerini getiren CQRS sorgusu.
 */
public record GetUserPlaylistsQuery(String userId) implements IQuery<List<PlaylistDetailDto>> {
    private static final Logger log = LoggerFactory.getLogger(GetUserPlaylistsQuery.class);

    @Component
    public static class Handler implements IQueryHandler<GetUserPlaylistsQuery, List<PlaylistDetailDto>> {
        private final IPlaylistService playlistService;

        public Handler(@Autowired(required = false) IPlaylistService playlistService) {
            this.playlistService = playlistService;
        }

        @Override
        public List<PlaylistDetailDto> handle(GetUserPlaylistsQuery query) {
            log.info("Executing GetUserPlaylistsQuery for user: {}", query.userId());
            if (playlistService != null && query.userId() != null) {
                return playlistService.getUserPlaylists(query.userId());
            }
            return Collections.emptyList();
        }
    }
}
