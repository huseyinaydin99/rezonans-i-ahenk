package tr.com.huseyinaydin.web.jsf;

import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.genre.commands.CreateGenreCommand;
import tr.com.huseyinaydin.application.genre.queries.GetSongsByGenreQuery;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;

import java.util.List;
import java.util.UUID;

@Component
@RequestScope
public class GenreBean {

    @Autowired
    private Mediator mediator;

    private UUID currentGenreId;
    private List<SongResponse> categorySongs;
    
    // For Admin Creation
    private String newGenreName;
    private String newGenreImageUrl;
    private String systemMessage;

    public void loadSongsForGenre() {
        if (currentGenreId != null) {
            this.categorySongs = mediator.query(new GetSongsByGenreQuery(currentGenreId));
        }
    }
    
    public void createGenre() {
        if (newGenreName != null && !newGenreName.trim().isEmpty()) {
            mediator.send(new CreateGenreCommand(newGenreName, newGenreImageUrl));
            this.systemMessage = "Müzik türü başarıyla oluşturuldu: " + newGenreName;
            this.newGenreName = "";
            this.newGenreImageUrl = "";
        }
    }

    // Getters and Setters
    public UUID getCurrentGenreId() { return currentGenreId; }
    public void setCurrentGenreId(UUID currentGenreId) { this.currentGenreId = currentGenreId; }
    public List<SongResponse> getCategorySongs() { return categorySongs; }
    public String getNewGenreName() { return newGenreName; }
    public void setNewGenreName(String newGenreName) { this.newGenreName = newGenreName; }
    public String getNewGenreImageUrl() { return newGenreImageUrl; }
    public void setNewGenreImageUrl(String newGenreImageUrl) { this.newGenreImageUrl = newGenreImageUrl; }
    public String getSystemMessage() { return systemMessage; }
}
