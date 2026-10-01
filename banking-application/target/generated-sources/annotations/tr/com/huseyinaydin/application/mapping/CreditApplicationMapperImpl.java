package tr.com.huseyinaydin.application.mapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.creditapplication.dtos.CreditApplicationResponse;
import tr.com.huseyinaydin.domain.creditapplication.CreditApplication;
import tr.com.huseyinaydin.domain.credittype.CreditType;
import tr.com.huseyinaydin.domain.enums.CreditApplicationStatus;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-20T20:33:56+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2.1 (Homebrew)"
)
@Component
public class CreditApplicationMapperImpl implements CreditApplicationMapper {

    @Override
    public CreditApplicationResponse toResponse(CreditApplication app, CreditType creditType) {
        if ( app == null && creditType == null ) {
            return null;
        }

        UUID id = null;
        LocalDateTime createdDate = null;
        UUID creditTypeId = null;
        BigDecimal requestedAmount = null;
        int requestedTerm = 0;
        BigDecimal interestRate = null;
        CreditApplicationStatus status = null;
        String rejectionReason = null;
        if ( app != null ) {
            id = app.getId();
            createdDate = app.getCreatedDate();
            creditTypeId = app.getCreditTypeId();
            requestedAmount = app.getRequestedAmount();
            requestedTerm = app.getRequestedTerm();
            interestRate = app.getInterestRate();
            status = app.getStatus();
            rejectionReason = app.getRejectionReason();
        }

        UUID customerId = app.getCustomerId();
        String customerName = app.getCustomer() != null ? app.getCustomer().getFullName() : "";
        String creditTypeName = creditType != null ? creditType.getName() : "";
        BigDecimal approvedAmount = app.getApprovedAmount() != null ? app.getApprovedAmount().getAmount() : null;
        BigDecimal monthlyPayment = app.getMonthlyPayment() != null ? app.getMonthlyPayment().getAmount() : null;
        BigDecimal totalPayment = app.getTotalPayment() != null ? app.getTotalPayment().getAmount() : null;

        CreditApplicationResponse creditApplicationResponse = new CreditApplicationResponse( id, customerId, customerName, creditTypeId, creditTypeName, requestedAmount, requestedTerm, approvedAmount, interestRate, monthlyPayment, totalPayment, status, rejectionReason, createdDate );

        return creditApplicationResponse;
    }
}
