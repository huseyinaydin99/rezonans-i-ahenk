package tr.com.huseyinaydin.application.membership.rules;

public interface IMembershipTierRules {
    int getMaxSkipCountPerHour();
    int getAudioQualityBitrate(); // e.g., 128, 256, 320
    boolean canListenOffline();
    boolean hasAds();
}
