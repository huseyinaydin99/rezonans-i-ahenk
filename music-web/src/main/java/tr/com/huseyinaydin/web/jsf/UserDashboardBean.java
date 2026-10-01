package tr.com.huseyinaydin.web.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
// Assumed queries exist for these
import java.util.Collections;
import java.util.List;

@Named
@RequestScoped
public class UserDashboardBean {
    @Autowired
    private Mediator mediator;

    private List<Object> recentHistory;
    private List<Object> likedSongs;

    @PostConstruct
    public void init() {
        // Fetching data using Mediator queries
        this.recentHistory = Collections.emptyList();
        this.likedSongs = Collections.emptyList();
    }
    
    public List<Object> getRecentHistory() { return recentHistory; }
    public List<Object> getLikedSongs() { return likedSongs; }
}
