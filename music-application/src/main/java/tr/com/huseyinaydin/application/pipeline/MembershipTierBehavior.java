package tr.com.huseyinaydin.application.pipeline;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.cqrs.IPipelineBehavior;
import tr.com.huseyinaydin.application.cqrs.PipelineDelegate;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException;

@Component
@Order(2) // Run after basic validation/auth
public class MembershipTierBehavior<TRequest, TResponse> implements IPipelineBehavior<TRequest, TResponse> {

    private final ICurrentUserService currentUserService;

    public MembershipTierBehavior(ICurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    public TResponse handle(TRequest request, PipelineDelegate<TResponse> next) {
        if (request instanceof ITierRestrictedRequest tierRequest) {
            MembershipTier requiredTier = tierRequest.getRequiredTier();
            
            // Assume ICurrentUserService has a method to get user's tier. Default to BASIC if null.
            MembershipTier userTier = currentUserService.getCurrentUserTier();
            if (userTier == null) {
                userTier = MembershipTier.BASIC;
            }

            if (userTier.ordinal() < requiredTier.ordinal()) {
                throw new AuthorizationException(
                    "TIER_RESTRICTION", 
                    "Bu içeriğe erişmek için en az " + requiredTier.name() + 
                    " paketine sahip olmalısınız. Mevcut paketiniz: " + userTier.name()
                );
            }
        }
        
        return next.proceed();
    }
}
