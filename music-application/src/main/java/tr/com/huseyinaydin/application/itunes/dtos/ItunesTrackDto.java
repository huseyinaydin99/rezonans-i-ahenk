package tr.com.huseyinaydin.application.itunes.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ItunesTrackDto(
    String trackName,
    String artistName,
    String artworkUrl100,
    String previewUrl,
    long trackTimeMillis,
    String primaryGenreName
) {}
