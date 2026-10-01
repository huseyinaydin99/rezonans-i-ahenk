package tr.com.huseyinaydin.application.interaction.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.interaction.SongLike;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record LikeSongCommand(UUID userId, UUID songId) implements ICommand<UUID> {
    @Component
    public static class Handler implements ICommandHandler<LikeSongCommand, UUID> {
        @Override
        public UUID handle(LikeSongCommand command) {
            SongLike like = new SongLike(command.userId(), command.songId());
            // repository.save(like);
            return like.getId();
        }
    }
}
