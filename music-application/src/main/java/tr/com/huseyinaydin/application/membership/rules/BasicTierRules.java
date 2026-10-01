package tr.com.huseyinaydin.application.membership.rules;

public class BasicTierRules implements IMembershipTierRules {
    @Override
    public int getMaxSkipCountPerHour() {
        return 6; // Basic kullanıcılar saatte max 6 şarkı atlayabilir
    }

    @Override
    public int getAudioQualityBitrate() {
        return 128; // Düşük ses kalitesi
    }

    @Override
    public boolean canListenOffline() {
        return false;
    }

    @Override
    public boolean hasAds() {
        return true; // Reklamlı
    }
}
