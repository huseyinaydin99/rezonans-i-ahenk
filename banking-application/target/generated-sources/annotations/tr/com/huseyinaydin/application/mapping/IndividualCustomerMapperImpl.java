package tr.com.huseyinaydin.application.mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.customers.dtos.IndividualCustomerResponse;
import tr.com.huseyinaydin.domain.customer.IndividualCustomer;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-20T20:33:56+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 26.0.2.1 (Homebrew)"
)
@Component
public class IndividualCustomerMapperImpl implements IndividualCustomerMapper {

    @Override
    public IndividualCustomerResponse toResponse(IndividualCustomer entity) {
        if ( entity == null ) {
            return null;
        }

        boolean isActive = false;
        UUID id = null;
        String firstName = null;
        String lastName = null;
        String nationalId = null;
        LocalDate dateOfBirth = null;
        String motherName = null;
        String fatherName = null;
        String phoneNumber = null;
        String email = null;
        String address = null;
        LocalDateTime createdDate = null;
        LocalDateTime updatedDate = null;

        isActive = entity.isActive();
        id = entity.getId();
        firstName = entity.getFirstName();
        lastName = entity.getLastName();
        nationalId = entity.getNationalId();
        dateOfBirth = entity.getDateOfBirth();
        motherName = entity.getMotherName();
        fatherName = entity.getFatherName();
        phoneNumber = entity.getPhoneNumber();
        email = entity.getEmail();
        address = entity.getAddress();
        createdDate = entity.getCreatedDate();
        updatedDate = entity.getUpdatedDate();

        IndividualCustomerResponse individualCustomerResponse = new IndividualCustomerResponse( id, firstName, lastName, nationalId, dateOfBirth, motherName, fatherName, phoneNumber, email, address, isActive, createdDate, updatedDate );

        return individualCustomerResponse;
    }
}
