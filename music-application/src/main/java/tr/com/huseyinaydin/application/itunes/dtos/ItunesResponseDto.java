package tr.com.huseyinaydin.application.itunes.dtos;

import java.util.List;

public record ItunesResponseDto(
    int resultCount,
    List<ItunesTrackDto> results
) {}
