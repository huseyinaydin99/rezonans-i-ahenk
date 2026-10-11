package tr.com.huseyinaydin.application.genre.queries;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record GetSongsByGenreQuery(UUID genreId) implements IQuery<List<SongResponse>>, ICacheableRequest {
    private static final Logger log = LoggerFactory.getLogger(GetSongsByGenreQuery.class);

    @Override
    public String getCacheKey() {
        return "genre:songs:" + genreId;
    }

    @Override
    public String getCacheName() {
        return "genresCache";
    }

    @Override
    public long getExpirationMinutes() {
        return 120;
    }

    @Component
    public static class Handler implements IQueryHandler<GetSongsByGenreQuery, List<SongResponse>> {
        private final ISongReadRepository readRepository;

        public Handler(ISongReadRepository readRepository) {
            this.readRepository = readRepository;
        }

        @Override
        public List<SongResponse> handle(GetSongsByGenreQuery query) {
            log.info("Fetching songs for Genre ID via CQRS Read Model: {}", query.genreId());
            List<SongReadModel> readModels = readRepository.findByGenreId(query.genreId());
            return readModels.stream()
                    .map(SongResponse::fromReadModel)
                    .collect(Collectors.toList());
        }
    }
}
