package tr.com.huseyinaydin.application.genre.dtos;

import java.io.Serializable;

public class GenreBrowseDto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String coverImageUrl;
    private String colorHex;

    public GenreBrowseDto(String id, String name, String coverImageUrl, String colorHex) {
        this.id = id;
        this.name = name;
        this.coverImageUrl = coverImageUrl;
        this.colorHex = colorHex;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public String getColorHex() { return colorHex; }
}
