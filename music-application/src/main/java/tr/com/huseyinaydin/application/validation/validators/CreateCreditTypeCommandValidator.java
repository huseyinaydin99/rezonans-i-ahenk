package tr.com.huseyinaydin.application.validation.validators;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.credittype.commands.CreateCreditTypeCommand;
import tr.com.huseyinaydin.application.validation.AbstractValidator;
import tr.com.huseyinaydin.application.validation.MusicValidationCodes;

import java.math.BigDecimal;

@Component
public class CreateCreditTypeCommandValidator
        extends AbstractValidator<CreateCreditTypeCommand> {

    private static final BigDecimal MIN_RATE = new BigDecimal("0.01");
    private static final BigDecimal MAX_RATE = new BigDecimal("99.99");

    public CreateCreditTypeCommandValidator() {

        addRule(r -> r.name() == null || r.name().isBlank(),
                "name", MusicValidationCodes.CREDIT_TYPE_NAME_REQUIRED);

        addRule(r -> r.customerType() == null,
                "customerType", MusicValidationCodes.CUSTOMER_TYPE_REQUIRED);

        addRule(r -> r.minimumAmount() == null,
                "minimumAmount", MusicValidationCodes.MIN_AMOUNT_REQUIRED);
        addRule(r -> r.minimumAmount() != null && r.minimumAmount().compareTo(BigDecimal.ZERO) <= 0,
                "minimumAmount", MusicValidationCodes.MIN_AMOUNT_MUST_BE_POSITIVE,
                r -> r.minimumAmount());

        addRule(r -> r.maximumAmount() == null,
                "maximumAmount", MusicValidationCodes.MAX_AMOUNT_REQUIRED);
        addRule(r -> r.maximumAmount() != null && r.maximumAmount().compareTo(BigDecimal.ZERO) <= 0,
                "maximumAmount", MusicValidationCodes.MAX_AMOUNT_MUST_BE_POSITIVE,
                r -> r.maximumAmount());

        addRule(r -> r.minimumAmount() != null && r.maximumAmount() != null
                        && r.minimumAmount().compareTo(r.maximumAmount()) >= 0,
                "minimumAmount", MusicValidationCodes.MIN_AMOUNT_EXCEEDS_MAX_AMOUNT,
                r -> r.minimumAmount());

        addRule(r -> r.minimumTermMonths() < 1,
                "minimumTermMonths", MusicValidationCodes.MIN_TERM_INVALID, r -> r.minimumTermMonths());

        addRule(r -> r.maximumTermMonths() < 1,
                "maximumTermMonths", MusicValidationCodes.MAX_TERM_INVALID, r -> r.maximumTermMonths());

        addRule(r -> r.minimumTermMonths() >= 1 && r.maximumTermMonths() >= 1
                        && r.minimumTermMonths() >= r.maximumTermMonths(),
                "minimumTermMonths", MusicValidationCodes.MIN_TERM_EXCEEDS_MAX_TERM, r -> r.minimumTermMonths());

        addRule(r -> r.annualInterestRate() == null,
                "annualInterestRate", MusicValidationCodes.INTEREST_RATE_REQUIRED);
        addRule(r -> r.annualInterestRate() != null
                        && (r.annualInterestRate().compareTo(MIN_RATE) < 0
                            || r.annualInterestRate().compareTo(MAX_RATE) > 0),
                "annualInterestRate", MusicValidationCodes.INTEREST_RATE_OUT_OF_RANGE,
                r -> r.annualInterestRate());
    }
}
