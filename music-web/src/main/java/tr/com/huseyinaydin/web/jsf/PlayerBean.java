package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.song.dtos.SongResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Oturum boyunca yaşayan çalma kuyruğu ve aktif şarkı durumu.
 * Sayfalar arasında gezinirken hangi şarkının çaldığı burada korunur;
 * saniye bazlı konum ise istemci tarafında (player.js) saklanır.
 */
@Named
@SessionScoped
public class PlayerBean implements Serializable {

    private final List<SongResponse> queue = new ArrayList<>();
    private int currentIndex = -1;
    private boolean isPlaying = false;

    /** Tek bir şarkıyı çalar; kuyrukta yoksa sona ekler. */
    public void playSong(SongResponse song) {
        if (song == null) {
            return;
        }
        int existing = indexOf(song);
        if (existing >= 0) {
            currentIndex = existing;
        } else {
            queue.add(song);
            currentIndex = queue.size() - 1;
        }
        isPlaying = true;
    }

    /** Bir listeyi (çalma listesi, albüm, keşfet) baştan veya verilen sıradan çalmaya başlar. */
    public void playQueue(List<SongResponse> songs, int startIndex) {
        queue.clear();
        if (songs == null || songs.isEmpty()) {
            currentIndex = -1;
            isPlaying = false;
            return;
        }
        queue.addAll(songs);
        currentIndex = Math.max(0, Math.min(startIndex, queue.size() - 1));
        isPlaying = true;
    }

    public void addToQueue(SongResponse song) {
        if (song != null && indexOf(song) < 0) {
            queue.add(song);
        }
    }

    public void next() {
        if (hasNext()) {
            currentIndex++;
            isPlaying = true;
        }
    }

    public void previous() {
        if (hasPrevious()) {
            currentIndex--;
            isPlaying = true;
        }
    }

    public void stopPlayback() {
        this.isPlaying = false;
    }

    public boolean hasNext() { return currentIndex >= 0 && currentIndex < queue.size() - 1; }
    public boolean hasPrevious() { return currentIndex > 0; }

    public SongResponse getCurrentSong() {
        return (currentIndex >= 0 && currentIndex < queue.size()) ? queue.get(currentIndex) : null;
    }

    public List<SongResponse> getQueue() { return Collections.unmodifiableList(queue); }
    public int getCurrentIndex() { return currentIndex; }
    public boolean isPlaying() { return isPlaying; }

    private int indexOf(SongResponse song) {
        for (int i = 0; i < queue.size(); i++) {
            if (queue.get(i).id().equals(song.id())) {
                return i;
            }
        }
        return -1;
    }
}
