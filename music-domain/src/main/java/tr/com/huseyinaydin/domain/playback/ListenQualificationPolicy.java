package tr.com.huseyinaydin.domain.playback;

/**
 * "Geçerli dinlenme" iş kuralı (Domain Policy).
 * Bir dinleme; en az 30 saniye sürdüyse VEYA şarkının yarısına ulaştıysa
 * dinlenme sayısına ve dinleme geçmişine dahil edilir.
 * Kural tek bir yerde durur; altyapı veya UI bu eşiği bilmek zorunda değildir.
 */
public final class ListenQualificationPolicy {

    static final long MIN_QUALIFYING_SECONDS = 30;
    static final double MIN_QUALIFYING_RATIO = 0.5;

    private ListenQualificationPolicy() { }

    public static boolean qualifies(long positionSeconds, long durationSeconds) {
        if (positionSeconds <= 0) {
            return false;
        }
        if (positionSeconds >= MIN_QUALIFYING_SECONDS) {
            return true;
        }
        return durationSeconds > 0 && ((double) positionSeconds / durationSeconds) >= MIN_QUALIFYING_RATIO;
    }
}
