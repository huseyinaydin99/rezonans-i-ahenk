package tr.com.huseyinaydin.application.artist.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.artist.Artist;
import tr.com.huseyinaydin.domain.repositories.IArtistRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record CreateArtistCommand(String name, String imageUrl, String bio) implements ICommand<UUID> {
    @Component
    public static class Handler implements ICommandHandler<CreateArtistCommand, UUID> {
        private final IArtistRepository repository;
        public Handler(IArtistRepository repository) { this.repository = repository; }
        @Override
        public UUID handle(CreateArtistCommand command) {
            Artist artist = new Artist(command.name(), command.imageUrl(), command.bio());
            repository.save(artist);
            return artist.getId();
        }
    }
}
