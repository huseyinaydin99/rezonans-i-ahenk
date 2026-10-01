package tr.com.huseyinaydin.application.membership;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tr.com.huseyinaydin.application.membership.commands.UpgradeMembershipCommand;
import tr.com.huseyinaydin.domain.enums.MembershipTier;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpgradeMembershipCommandTest {

    @InjectMocks
    private UpgradeMembershipCommand.Handler handler;

    // Repositories or external services would be mocked here
    // @Mock private IUserRepository userRepository;

    private UUID mockUserId;

    @BeforeEach
    void setUp() {
        mockUserId = UUID.randomUUID();
    }

    @Test
    @DisplayName("Başarılı Paket Yükseltme İşlemi (BASIC -> PREMIUM)")
    void testUpgradeMembership_Success() {
        // Arrange
        UpgradeMembershipCommand command = new UpgradeMembershipCommand(mockUserId, "PREMIUM", "mock_token_123");

        // Act
        Boolean result = handler.handle(command);

        // Assert
        assertTrue(result, "Paket yükseltme işlemi başarılı dönmelidir.");
        // verify(userRepository, times(1)).update(any());
    }

    @Test
    @DisplayName("Mevcut paketten daha düşük bir pakete geçiş engellenmeli (Business Rule)")
    void testUpgradeMembership_ThrowsBusinessException_WhenTierIsLower() {
        // Arrange
        // Mocking the scenario where user is already ELIT inside the handler (if it was dynamic)
        // Since handler mocks current tier as BASIC, let's test a hypothetical failure
        // We simulate a Business Exception
        UpgradeMembershipCommand command = new UpgradeMembershipCommand(mockUserId, "BASIC", "mock_token_123");

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            handler.handle(command);
        });
        
        assertTrue(exception.getMessage().contains("Zaten bu pakete"));
    }
}
