package tr.com.huseyinaydin.application.interaction.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.history.ListeningHistory;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;
import java.util.UUID;

public record RecordListeningHistoryCommand(UUID userId, UUID songId) implements ICommand<UUID> {
    @Component
    public static class Handler implements ICommandHandler<RecordListeningHistoryCommand, UUID> {
        @Override
        public UUID handle(RecordListeningHistoryCommand command) {
            ListeningHistory history = new ListeningHistory(command.userId(), command.songId());
            // repository.save(history);
            return history.getId();
        }
    }
}
