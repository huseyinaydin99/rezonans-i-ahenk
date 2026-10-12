package tr.com.huseyinaydin.application.itunes.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItunesResponseDto(
    int resultCount,
    List<ItunesTrackDto> results
) {}
