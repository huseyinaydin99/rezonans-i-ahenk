package tr.com.huseyinaydin.persistence.seed;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tr.com.huseyinaydin.domain.enums.UserRole;
import tr.com.huseyinaydin.domain.user.ApplicationUser;
import tr.com.huseyinaydin.domain.user.OperationClaim;
import tr.com.huseyinaydin.domain.user.UserOperationClaim;
import tr.com.huseyinaydin.sharedkernel.security.GeneralOperationClaims;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Component
public class DataInitializer {

    @PersistenceContext
    private EntityManager entityManager;

    private final TransactionTemplate transactionTemplate;

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DataInitializer.class);

    public DataInitializer(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void seedData() {
        try {
            transactionTemplate.execute(status -> {
                // Only seed if empty
                Long count = entityManager.createQuery("SELECT COUNT(o) FROM OperationClaim o", Long.class).getSingleResult();
                if (count > 0) {
                    return null;
                }

                String[] claims = {
                        GeneralOperationClaims.INDIVIDUAL_CUSTOMERS_CREATE,
                        GeneralOperationClaims.INDIVIDUAL_CUSTOMERS_UPDATE,
                        GeneralOperationClaims.INDIVIDUAL_CUSTOMERS_READ,
                        GeneralOperationClaims.CORPORATE_CUSTOMERS_CREATE,
                        GeneralOperationClaims.CREDIT_APPLICATIONS_CREATE,
                        GeneralOperationClaims.CREDIT_APPLICATIONS_APPROVE,
                        GeneralOperationClaims.CREDIT_APPLICATIONS_READ
                };

                Map<String, OperationClaim> claimMap = new HashMap<>();
                for (String claimName : claims) {
                    OperationClaim claim = new OperationClaim(claimName);
                    entityManager.persist(claim);
                    claimMap.put(claimName, claim);
                }

                List<ApplicationUser> users = entityManager.createQuery("SELECT u FROM ApplicationUser u", ApplicationUser.class).getResultList();
                
                for (ApplicationUser user : users) {
                    assignClaimsToUser(user, claimMap);
                }
                log.info("✅ [DATA INITIALIZER] Başlangıç yetki ve rol claim'leri başarıyla veritabanına yazıldı.");
                return null;
            });
        } catch (Exception e) {
            log.warn("⚠️ [DATA INITIALIZER] Başlangıç verileri oluşturulurken hata/uyarı: {}", e.getMessage());
        }
    }

    private void assignClaimsToUser(ApplicationUser user, Map<String, OperationClaim> claimMap) {
        if (user.getRole() == UserRole.ADMIN) {
            for (OperationClaim claim : claimMap.values()) {
                entityManager.persist(new UserOperationClaim(user.getId(), claim.getId()));
            }
        } else if (user.getRole() == UserRole.OFFICER) {
            String[] officerClaims = {
                    GeneralOperationClaims.INDIVIDUAL_CUSTOMERS_CREATE,
                    GeneralOperationClaims.INDIVIDUAL_CUSTOMERS_UPDATE,
                    GeneralOperationClaims.INDIVIDUAL_CUSTOMERS_READ,
                    GeneralOperationClaims.CORPORATE_CUSTOMERS_CREATE,
                    GeneralOperationClaims.CREDIT_APPLICATIONS_CREATE,
                    GeneralOperationClaims.CREDIT_APPLICATIONS_APPROVE,
                    GeneralOperationClaims.CREDIT_APPLICATIONS_READ
            };
            for (String claimName : officerClaims) {
                entityManager.persist(new UserOperationClaim(user.getId(), claimMap.get(claimName).getId()));
            }
        } else if (user.getRole() == UserRole.CUSTOMER) {
            entityManager.persist(new UserOperationClaim(user.getId(), claimMap.get(GeneralOperationClaims.CREDIT_APPLICATIONS_READ).getId()));
        }
    }
}
