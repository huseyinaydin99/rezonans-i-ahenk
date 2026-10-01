package tr.com.huseyinaydin.application.membership.rules;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.domain.enums.MembershipTier;

@Component
public class MembershipTierFactory {

    /**
     * Gelen paket (Tier) enum değerine göre devasa if-else bloklarına girmeden 
     * nesne yönelimli olarak ilgili kural sınıfını anında üretir (Factory Pattern).
     */
    public IMembershipTierRules getRulesForTier(MembershipTier tier) {
        if (tier == null) {
            return new BasicTierRules(); // Varsayılan kural
        }

        return switch (tier) {
            case PREMIUM -> new PremiumTierRules();
            case ELITE -> new EliteTierRules();
            case BASIC -> new BasicTierRules();
            default -> new BasicTierRules();
        };
    }
}
