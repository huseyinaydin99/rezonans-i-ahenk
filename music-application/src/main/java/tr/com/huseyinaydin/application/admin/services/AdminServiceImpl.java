package tr.com.huseyinaydin.application.admin.services;

import org.springframework.stereotype.Service;
import tr.com.huseyinaydin.application.ports.IAdminService;
import tr.com.huseyinaydin.application.admin.dtos.UserRoleDto;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminServiceImpl implements IAdminService {

    @Override
    public List<UserRoleDto> getAllUsers() {
        System.out.println("[ADMIN PANEL] Tüm kullanıcı listesi çekiliyor...");
        
        List<UserRoleDto> users = new ArrayList<>();
        users.add(new UserRoleDto(UUID.randomUUID().toString(), "huseyinaydin", "admin@rezonans.com", "ROLE_ADMIN", false));
        users.add(new UserRoleDto(UUID.randomUUID().toString(), "theweeknd", "contact@theweeknd.com", "ROLE_ARTIST", false));
        users.add(new UserRoleDto(UUID.randomUUID().toString(), "normal_user_1", "user@test.com", "ROLE_USER", false));
        users.add(new UserRoleDto(UUID.randomUUID().toString(), "spammer_99", "spam@bot.com", "ROLE_USER", true));
        
        return users;
    }

    @Override
    public void assignRole(String userId, String newRole) {
        System.out.println("[ADMIN PANEL] Kullanıcı (" + userId + ") için rol değişikliği tetiklendi. Yeni Rol: " + newRole);
        // Gerçek senaryoda burada Keycloak Admin REST API'si çağrılarak 
        // kullanıcının "realm_access.roles" dizisi güncellenir.
        System.out.println("-> Keycloak sunucusu ile asenkron iletişim kuruldu ve rol güncellendi.");
    }

    @Override
    public void toggleUserBanStatus(String userId, boolean status) {
        System.out.println("[ADMIN PANEL] Kullanıcı (" + userId + ") Ban Durumu: " + (status ? "YASAKLANDI" : "YASAK KALDIRILDI"));
        // Keycloak üzerinden kullanıcının 'enabled' statüsü false/true yapılır.
    }
}
