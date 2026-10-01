package tr.com.huseyinaydin.web.jsf;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.queries.GetListSongQuery;

import java.util.List;

@Named
@RequestScoped
public class SongBean {

    @Autowired
    private Mediator mediator;

    private List<SongResponse> topSongs;

    @PostConstruct
    public void init() {
        this.topSongs = mediator.send(new GetListSongQuery());
    }

    public List<SongResponse> getTopSongs() {
        return topSongs;
    }
}
