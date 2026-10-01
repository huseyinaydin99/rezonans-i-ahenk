package tr.com.huseyinaydin.application.notifications.observer;

/**
 * Gözlemcileri (Takipçileri) yönetecek ve bildirim gönderecek Yayıncı arayüzü
 */
public interface ISubject {
    void attach(IObserver observer);
    void detach(IObserver observer);
    void notifyObservers(Song newSong);
}
