package tr.com.huseyinaydin.web.jsf;

import jakarta.annotation.PostConstruct;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import tr.com.huseyinaydin.application.song.queries.GetListSongQuery;

import java.util.List;

@Component
@RequestScope
public class SongBean {

    @Autowired
    private Mediator mediator;

    private List<SongResponse> topSongs;

    @PostConstruct
    public void init() {
        this.topSongs = mediator.query(new GetListSongQuery());
    }

    public List<SongResponse> getTopSongs() {
        return topSongs;
    }
}
