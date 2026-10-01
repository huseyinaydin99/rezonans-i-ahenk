package tr.com.huseyinaydin.application.itunes.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IItunesService;
import tr.com.huseyinaydin.application.itunes.dtos.ItunesResponseDto;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

public record SearchItunesQuery(String term, int limit) implements IQuery<ItunesResponseDto> {
    
    @Component
    public static class Handler implements IQueryHandler<SearchItunesQuery, ItunesResponseDto> {
        private final IItunesService itunesService;

        public Handler(IItunesService itunesService) {
            this.itunesService = itunesService;
        }

        @Override
        public ItunesResponseDto handle(SearchItunesQuery query) {
            return itunesService.searchMusic(query.term(), query.limit());
        }
    }
}
