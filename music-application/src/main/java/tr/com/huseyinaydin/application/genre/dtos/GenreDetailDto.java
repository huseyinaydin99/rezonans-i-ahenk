package tr.com.huseyinaydin.application.genre.dtos;

import java.io.Serializable;
import java.util.List;

public class GenreDetailDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String genreName;
    private List<String> topSongs;

    public GenreDetailDto(String genreName, List<String> topSongs) {
        this.genreName = genreName;
        this.topSongs = topSongs;
    }

    public String getGenreName() { return genreName; }
    public List<String> getTopSongs() { return topSongs; }
}
