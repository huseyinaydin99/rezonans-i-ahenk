package tr.com.huseyinaydin.application.streaming;

import tr.com.huseyinaydin.domain.song.Song;

/**
 * Hiçbir filtreye veya reklama maruz kalmamış ham, saf ses dosyası (Concrete Component)
 */
public class BaseAudioStream implements IAudioStream {
    private final Song song;

    public BaseAudioStream(Song song) {
        this.song = song;
    }

    @Override
    public String play() {
        return "[SAF SES] " + song.getTitle() + " çalınıyor... (Bitrate: 128kbps)";
    }
}
