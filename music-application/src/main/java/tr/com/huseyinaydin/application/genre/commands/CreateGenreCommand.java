package tr.com.huseyinaydin.application.genre.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.genre.Genre;
import tr.com.huseyinaydin.domain.repositories.IGenreRepository;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.util.UUID;

public record CreateGenreCommand(String name, String imageUrl) implements ICommand<UUID> {
    
    @Component
    public static class Handler implements ICommandHandler<CreateGenreCommand, UUID> {
        private final IGenreRepository genreRepository;

        public Handler(IGenreRepository genreRepository) {
            this.genreRepository = genreRepository;
        }

        @Override
        public UUID handle(CreateGenreCommand command) {
            Genre genre = new Genre(command.name(), command.imageUrl());
            genreRepository.save(genre);
            System.out.println("Yeni Tür Oluşturuldu: " + genre.getName());
            return genre.getId();
        }
    }
}
