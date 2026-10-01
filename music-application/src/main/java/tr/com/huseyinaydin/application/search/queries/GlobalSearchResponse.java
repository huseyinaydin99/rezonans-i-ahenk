package tr.com.huseyinaydin.application.search.queries;

import tr.com.huseyinaydin.application.song.dtos.SongResponse;
// Assuming simple DTOs exist for Artist and Playlist for architecture demonstration
import java.util.List;

public record GlobalSearchResponse(
    List<SongResponse> songs,
    List<Object> artists,
    List<Object> playlists
) {}
