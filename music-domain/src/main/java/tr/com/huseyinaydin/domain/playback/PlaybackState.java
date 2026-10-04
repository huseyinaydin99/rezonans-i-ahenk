package tr.com.huseyinaydin.domain.playback;

/**
 * Bir dinleme oturumunun yaşam döngüsündeki anlık durumu.
 * İstemci (oynatıcı) her durum geçişinde sunucuya bu değerlerden birini bildirir.
 */
public enum PlaybackState {
    STARTED,
    PROGRESS,
    PAUSED,
    SKIPPED,
    COMPLETED;

    public boolean isTerminal() {
        return this == SKIPPED || this == COMPLETED;
    }
}
