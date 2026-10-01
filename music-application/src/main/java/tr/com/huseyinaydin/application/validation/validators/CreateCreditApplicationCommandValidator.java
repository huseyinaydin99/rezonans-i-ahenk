package tr.com.huseyinaydin.application.validation.validators;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.creditapplication.commands.CreateCreditApplicationCommand;
import tr.com.huseyinaydin.application.validation.AbstractValidator;
import tr.com.huseyinaydin.application.validation.MusicValidationCodes;

import java.math.BigDecimal;

@Component
public class CreateCreditApplicationCommandValidator
        extends AbstractValidator<CreateCreditApplicationCommand> {

    private static final BigDecimal MAX_AMOUNT = new BigDecimal("10000000");

    public CreateCreditApplicationCommandValidator() {

        addRule(r -> r.customerId() == null,
                "customerId", MusicValidationCodes.CUSTOMER_ID_REQUIRED);

        addRule(r -> r.creditTypeId() == null,
                "creditTypeId", MusicValidationCodes.CREDIT_TYPE_ID_REQUIRED);

        addRule(r -> r.requestedAmount() == null,
                "requestedAmount", MusicValidationCodes.AMOUNT_REQUIRED);
        addRule(r -> r.requestedAmount() != null
                        && r.requestedAmount().compareTo(BigDecimal.ZERO) <= 0,
                "requestedAmount", MusicValidationCodes.AMOUNT_MUST_BE_POSITIVE,
                r -> r.requestedAmount());
        addRule(r -> r.requestedAmount() != null
                        && r.requestedAmount().compareTo(MAX_AMOUNT) > 0,
                "requestedAmount", MusicValidationCodes.AMOUNT_EXCEEDS_MAX,
                r -> r.requestedAmount());

        addRule(r -> r.requestedTerm() < 1 || r.requestedTerm() > 360,
                "requestedTerm", MusicValidationCodes.TERM_OUT_OF_RANGE,
                r -> r.requestedTerm());
    }
}
