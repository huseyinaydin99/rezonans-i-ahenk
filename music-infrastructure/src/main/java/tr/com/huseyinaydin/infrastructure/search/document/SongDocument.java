package tr.com.huseyinaydin.infrastructure.search.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "songs_index")
public class SongDocument {
    
    @Id
    private String id;
    
    @Field(type = FieldType.Text, analyzer = "standard")
    private String title;
    
    @Field(type = FieldType.Keyword)
    private String artistName;
    
    @Field(type = FieldType.Text)
    private String genreName;

    public SongDocument(String id, String title, String artistName, String genreName) {
        this.id = id;
        this.title = title;
        this.artistName = artistName;
        this.genreName = genreName;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtistName() { return artistName; }
}
