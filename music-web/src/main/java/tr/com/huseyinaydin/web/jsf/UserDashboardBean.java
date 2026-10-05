package tr.com.huseyinaydin.web.jsf;

import jakarta.annotation.PostConstruct;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.queries.GetRecommendedSongsQuery;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Component
@RequestScope
public class UserDashboardBean {
    @Autowired
    private Mediator mediator;

    private List<Object> recentHistory;
    private List<Object> likedSongs;
    private List<SongResponse> recommendedSongs;

    @PostConstruct
    public void init() {
        this.recentHistory = Collections.emptyList();
        this.likedSongs = Collections.emptyList();
        
        // Assume current logged-in user UUID
        UUID currentUserId = UUID.randomUUID(); 
        this.recommendedSongs = mediator.query(new GetRecommendedSongsQuery(currentUserId, 5));
    }
    
    public List<Object> getRecentHistory() { return recentHistory; }
    public List<Object> getLikedSongs() { return likedSongs; }
    public List<SongResponse> getRecommendedSongs() { return recommendedSongs; }
}
