package tr.com.huseyinaydin.application.mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.credittype.dtos.CreditTypeResponse;
import tr.com.huseyinaydin.domain.credittype.CreditType;
import tr.com.huseyinaydin.domain.enums.CustomerType;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-20T20:33:56+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2.1 (Homebrew)"
)
@Component
public class CreditTypeMapperImpl implements CreditTypeMapper {

    @Override
    public CreditTypeResponse toResponseFlat(CreditType entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        String name = null;
        String description = null;
        CustomerType customerType = null;
        int minimumTermMonths = 0;
        int maximumTermMonths = 0;
        BigDecimal annualInterestRate = null;
        LocalDateTime createdDate = null;

        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        customerType = entity.getCustomerType();
        minimumTermMonths = entity.getMinimumTermMonths();
        maximumTermMonths = entity.getMaximumTermMonths();
        annualInterestRate = entity.getAnnualInterestRate();
        createdDate = entity.getCreatedDate();

        BigDecimal minimumAmount = entity.getMinimumAmount() != null ? entity.getMinimumAmount().getAmount() : null;
        BigDecimal maximumAmount = entity.getMaximumAmount() != null ? entity.getMaximumAmount().getAmount() : null;
        UUID parentCreditTypeId = entity.getParentCreditTypeId();
        List<CreditTypeResponse> subCreditTypes = null;

        CreditTypeResponse creditTypeResponse = new CreditTypeResponse( id, name, description, customerType, minimumAmount, maximumAmount, minimumTermMonths, maximumTermMonths, annualInterestRate, parentCreditTypeId, subCreditTypes, createdDate );

        return creditTypeResponse;
    }
}
