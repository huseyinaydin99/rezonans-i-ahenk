package tr.com.huseyinaydin.application.song.commands;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.rules.SongBusinessRules;
import tr.com.huseyinaydin.domain.repositories.ISongRepository;
import tr.com.huseyinaydin.domain.song.Song;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommandHandler;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateSongCommand(
        @NotBlank String title,
        @NotBlank String coverImageUrl,
        @NotBlank String audioUrl,
        @NotNull @Positive long durationSeconds,
        @NotNull String tierName,
        @NotNull LocalDateTime releaseDate,
        @NotNull UUID artistId,
        @NotNull UUID genreId
) implements ICommand<SongResponse> {

    @Component
    public static class Handler implements ICommandHandler<CreateSongCommand, SongResponse> {

        private final ISongRepository songRepository;
        private final SongBusinessRules businessRules;

        public Handler(ISongRepository songRepository, SongBusinessRules businessRules) {
            this.songRepository = songRepository;
            this.businessRules = businessRules;
        }

        @Override
        public SongResponse handle(CreateSongCommand command) {
            businessRules.artistMustExist(command.artistId());
            businessRules.genreMustExist(command.genreId());

            Song song = Song.builder()
                    .title(command.title())
                    .coverImageUrl(command.coverImageUrl())
                    .audioUrl(command.audioUrl())
                    .duration(Duration.ofSeconds(command.durationSeconds()))
                    .tier(MembershipTier.valueOf(command.tierName().toUpperCase()))
                    .releaseDate(command.releaseDate())
                    .artistId(command.artistId())
                    .genreId(command.genreId())
                    .build();

            songRepository.save(song);

            return new SongResponse(
                    song.getId(),
                    song.getTitle(),
                    song.getCoverImageUrl(),
                    song.getAudioUrl(),
                    song.getDuration().getSeconds(),
                    song.getPlayCount(),
                    song.getTier().name(),
                    song.getReleaseDate(),
                    song.getArtistId(),
                    song.getGenreId()
            );
        }
    }
}
