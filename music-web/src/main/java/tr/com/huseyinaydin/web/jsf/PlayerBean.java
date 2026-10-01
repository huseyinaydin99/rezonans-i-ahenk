package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;
import java.io.Serializable;

@Named
@SessionScoped
public class PlayerBean implements Serializable {

    private SongResponse currentSong;
    private boolean isPlaying = false;

    public void playSong(SongResponse song) {
        this.currentSong = song;
        this.isPlaying = true;
        System.out.println("Şimdi çalıyor: " + song.title() + " - " + song.audioUrl());
    }
    
    public void stopPlayback() {
        this.isPlaying = false;
    }

    public SongResponse getCurrentSong() { return currentSong; }
    public boolean isPlaying() { return isPlaying; }
}
