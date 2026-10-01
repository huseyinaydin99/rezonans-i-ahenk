package tr.com.huseyinaydin.application.notifications.observer;

import tr.com.huseyinaydin.domain.song.Song;

/**
 * Güncellemeleri dinleyecek olan Gözlemci arayüzü
 */
public interface IObserver {
    void update(String artistName, Song newSong);
}
