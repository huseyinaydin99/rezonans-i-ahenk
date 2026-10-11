package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.domain.repositories.ISongReadRepository;
import tr.com.huseyinaydin.domain.song.read.SongReadModel;
import tr.com.huseyinaydin.application.pipeline.ICacheableRequest;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.List;
import java.util.stream.Collectors;

public record GetListSongQuery() implements IQuery<List<SongResponse>>, ICacheableRequest {

    @Override
    public String getCacheKey() {
        return "songs:list:all";
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
    public static class Handler implements IQueryHandler<GetListSongQuery, List<SongResponse>> {
        private final ISongReadRepository readRepository;

        public Handler(ISongReadRepository readRepository) {
            this.readRepository = readRepository;
        }
        
        @Override
        public List<SongResponse> handle(GetListSongQuery query) {
            List<SongReadModel> readModels = readRepository.findAll(0, 50);
            return readModels.stream()
                    .map(SongResponse::fromReadModel)
                    .collect(Collectors.toList());
        }
    }
}
