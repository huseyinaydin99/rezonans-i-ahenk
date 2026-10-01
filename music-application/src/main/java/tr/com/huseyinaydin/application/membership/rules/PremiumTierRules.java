package tr.com.huseyinaydin.application.membership.rules;

public class PremiumTierRules implements IMembershipTierRules {
    @Override
    public int getMaxSkipCountPerHour() {
        return Integer.MAX_VALUE; // Sınırsız atlama
    }

    @Override
    public int getAudioQualityBitrate() {
        return 256; // Yüksek ses kalitesi
    }

    @Override
    public boolean canListenOffline() {
        return true; // Çevrimdışı dinleyebilir
    }

    @Override
    public boolean hasAds() {
        return false; // Reklamsız
    }
}
