package tr.com.huseyinaydin.application.mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.customers.dtos.CorporateCustomerResponse;
import tr.com.huseyinaydin.domain.customer.CorporateCustomer;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-20T20:33:56+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2.1 (Homebrew)"
)
@Component
public class CorporateCustomerMapperImpl implements CorporateCustomerMapper {

    @Override
    public CorporateCustomerResponse toResponse(CorporateCustomer entity) {
        if ( entity == null ) {
            return null;
        }

        boolean isActive = false;
        UUID id = null;
        String companyName = null;
        String taxNumber = null;
        String tradeRegistrationNumber = null;
        String taxOffice = null;
        String companyRegistrationNumber = null;
        String authorizedPersonName = null;
        LocalDate companyFoundationDate = null;
        String phoneNumber = null;
        String email = null;
        LocalDateTime createdDate = null;

        isActive = entity.isActive();
        id = entity.getId();
        companyName = entity.getCompanyName();
        taxNumber = entity.getTaxNumber();
        tradeRegistrationNumber = entity.getTradeRegistrationNumber();
        taxOffice = entity.getTaxOffice();
        companyRegistrationNumber = entity.getCompanyRegistrationNumber();
        authorizedPersonName = entity.getAuthorizedPersonName();
        companyFoundationDate = entity.getCompanyFoundationDate();
        phoneNumber = entity.getPhoneNumber();
        email = entity.getEmail();
        createdDate = entity.getCreatedDate();

        CorporateCustomerResponse corporateCustomerResponse = new CorporateCustomerResponse( id, companyName, taxNumber, tradeRegistrationNumber, taxOffice, companyRegistrationNumber, authorizedPersonName, companyFoundationDate, phoneNumber, email, isActive, createdDate );

        return corporateCustomerResponse;
    }
}
