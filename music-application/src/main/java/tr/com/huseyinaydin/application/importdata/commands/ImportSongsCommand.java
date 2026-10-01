package tr.com.huseyinaydin.application.importdata.commands;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ImportSongsCommand(String csvContent) implements ICommand<Integer> {
    
    @Component
    public static class Handler implements ICommandHandler<ImportSongsCommand, Integer> {
        
        private final ISongRepository songRepository;

        public Handler(ISongRepository songRepository) {
            this.songRepository = songRepository;
        }

        @Override
        public Integer handle(ImportSongsCommand command) {
            String[] lines = command.csvContent().split("\n");
            List<Song> songsToSave = new ArrayList<>();
            
            // Skip header line assuming "Title,AudioUrl,Duration,Tier,ArtistId,GenreId"
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].trim().isEmpty()) continue;
                
                String[] columns = lines[i].split(",");
                if (columns.length >= 6) {
                    try {
                        Song song = new Song(
                            columns[0].trim(), // Title
                            "default_cover.jpg", // Cover
                            columns[1].trim(), // AudioUrl
                            Duration.ofSeconds(Long.parseLong(columns[2].trim())), // Duration
                            MembershipTier.valueOf(columns[3].trim().toUpperCase()), // Tier
                            LocalDateTime.now(),
                            UUID.fromString(columns[4].trim()), // ArtistId
                            UUID.fromString(columns[5].trim())  // GenreId
                        );
                        songsToSave.add(song);
                    } catch (Exception e) {
                        System.err.println("Satır atlandı, hatalı format: " + lines[i]);
                    }
                }
            }
            
            // Toplu kaydetme (Batch Save)
            for (Song song : songsToSave) {
                songRepository.save(song);
            }
            
            return songsToSave.size();
        }
    }
}
