package tr.com.huseyinaydin.application.itunes.dtos;

public record ItunesTrackDto(
    String trackName,
    String artistName,
    String artworkUrl100,
    String previewUrl,
    long trackTimeMillis,
    String primaryGenreName
) {}
