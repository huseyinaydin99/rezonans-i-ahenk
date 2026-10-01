package tr.com.huseyinaydin.application.song.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.pipeline.ITierRestrictedRequest;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.rules.SongBusinessRules;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

import java.util.UUID;

public record PlaySongQuery(UUID id, MembershipTier songTier) implements IQuery<String>, ITierRestrictedRequest {
    
    @Override
    public MembershipTier getRequiredTier() {
        return songTier; // Dynamic restriction based on the song's tier
    }

    @Component
    public static class Handler implements IQueryHandler<PlaySongQuery, String> {
        private final ISongRepository songRepository;
        private final SongBusinessRules businessRules;

        public Handler(ISongRepository songRepository, SongBusinessRules businessRules) {
            this.songRepository = songRepository;
            this.businessRules = businessRules;
        }

        @Override
        public String handle(PlaySongQuery query) {
            businessRules.songMustExistWhenRequested(query.id());
            
            Song song = songRepository.findById(query.id()).get();
            song.incrementPlayCount();
            songRepository.update(song); // Dinlenme sayısını artır
            
            // URL'yi dön (Client bu URL ile müziği çalacak)
            return song.getAudioUrl();
        }
    }
}
