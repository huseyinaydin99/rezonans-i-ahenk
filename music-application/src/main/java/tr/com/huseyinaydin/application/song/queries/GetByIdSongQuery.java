package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;
import tr.com.huseyinaydin.sharedkernel.exception.NotFoundException;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.UUID;

public record GetByIdSongQuery(UUID id) implements IQuery<SongResponse>, ICacheableRequest {

    @Override
    public String getCacheKey() {
        return "song:" + id;
    }

    @Override
    public String getCacheName() {
        return "songsCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 60;
    }

    @Component
    public static class Handler implements IQueryHandler<GetByIdSongQuery, SongResponse> {

        private final ISongReadRepository readRepository;

        public Handler(ISongReadRepository readRepository) {
            this.readRepository = readRepository;
        }

        @Override
        public SongResponse handle(GetByIdSongQuery query) {
            return readRepository.findById(query.id())
                    .map(SongResponse::fromReadModel)
                    .orElseThrow(() -> new NotFoundException("SONG", query.id() != null ? query.id().toString() : "null"));
        }
    }
}
