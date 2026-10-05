package tr.com.huseyinaydin.web.beans;

import org.springframework.web.context.annotation.RequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.IHomeService;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;

@Component
@RequestScope
public class HomeBean {

    @Autowired
    private IHomeService homeService;

    private HomeDashboardDto dashboardData;

    public void initDashboard() {
        if (this.dashboardData == null) {
            String userId = "current-logged-in-user"; // SecurityContext
            this.dashboardData = homeService.buildHomeDashboard(userId);
        }
    }

    public HomeDashboardDto getDashboardData() { return dashboardData; }
}
