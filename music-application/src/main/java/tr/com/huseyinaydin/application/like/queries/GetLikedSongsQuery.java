package tr.com.huseyinaydin.application.like.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.ILikeRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;
import java.util.List;

public record GetLikedSongsQuery(String userEmail) implements IQuery<List<Long>> {
    @Component
    public static class Handler implements IQueryHandler<GetLikedSongsQuery, List<Long>> {
        private final ILikeRepository likeRepository;

        public Handler(ILikeRepository likeRepository) {
            this.likeRepository = likeRepository;
        }

        @Override
        public List<Long> handle(GetLikedSongsQuery query) {
            return likeRepository.getLikedSongIds(query.userEmail());
        }
    }
}
