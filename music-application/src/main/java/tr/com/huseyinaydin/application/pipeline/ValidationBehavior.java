package tr.com.huseyinaydin.application.pipeline;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.cqrs.IPipelineBehavior;
import tr.com.huseyinaydin.application.cqrs.PipelineDelegate;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Order(2) // Runs after Logging, before Caching
public class ValidationBehavior implements IPipelineBehavior {

    private final Validator validator;

    public ValidationBehavior() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Override
    public <TRequest, TResponse> TResponse handle(TRequest request, PipelineDelegate<TResponse> next) {
        
        Set<ConstraintViolation<TRequest>> violations = validator.validate(request);
        
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.joining(", "));
            
            System.err.println("[VALIDATION_FAILED] " + request.getClass().getSimpleName() + " -> " + errors);
            throw new BusinessException("Validasyon Hatası: " + errors);
        }
        
        return next.handle();
    }
}
