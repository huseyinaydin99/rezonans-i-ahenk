package tr.com.huseyinaydin.application.song.commands;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.rules.SongBusinessRules;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSongCommandHandlerTest {

    @Mock
    private ISongRepository songRepository;

    @Mock
    private SongBusinessRules businessRules;

    @InjectMocks
    private CreateSongCommand.Handler handler;

    @Test
    void handle_ShouldCreateSongAndReturnResponse() {
        // Arrange
        CreateSongCommand command = new CreateSongCommand(
                "Test Song",
                "cover.jpg",
                "audio.mp3",
                210L,
                "PREMIUM",
                LocalDateTime.now(),
                UUID.randomUUID(),
                UUID.randomUUID()
        );

        when(songRepository.save(any(Song.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        SongResponse response = handler.handle(command);

        // Assert
        assertNotNull(response);
        assertNotNull(response.id());
        verify(businessRules).artistMustExist(command.artistId());
        verify(businessRules).genreMustExist(command.genreId());
        verify(songRepository).save(any(Song.class));
    }
}
