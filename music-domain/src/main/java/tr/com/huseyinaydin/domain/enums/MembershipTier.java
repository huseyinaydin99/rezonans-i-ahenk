package tr.com.huseyinaydin.domain.enums;

public enum MembershipTier {
    BASIC,
    GOLD,
    PREMIUM,
    ELIT;

    public boolean canAccess(MembershipTier requiredTier) {
        if (requiredTier == null) return true;
        return this.ordinal() >= requiredTier.ordinal();
    }

    public static MembershipTier fromString(String tier) {
        if (tier == null || tier.isBlank()) return BASIC;
        String t = tier.trim().toUpperCase(java.util.Locale.ROOT);
        if (t.equals("ELITE")) return ELIT;
        try {
            return MembershipTier.valueOf(t);
        } catch (IllegalArgumentException e) {
            return BASIC;
        }
    }
}
