package tr.com.huseyinaydin.application.membership.rules;

public class EliteTierRules implements IMembershipTierRules {
    @Override
    public int getMaxSkipCountPerHour() {
        return Integer.MAX_VALUE; // Sınırsız atlama
    }

    @Override
    public int getAudioQualityBitrate() {
        return 320; // Kayıpsız (Hi-Fi) üst düzey ses kalitesi
    }

    @Override
    public boolean canListenOffline() {
        return true; 
    }

    @Override
    public boolean hasAds() {
        return false; 
    }
}
