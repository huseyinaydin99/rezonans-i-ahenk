package tr.com.huseyinaydin.web.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import tr.com.huseyinaydin.application.ports.IHomeService;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;

@Named
@RequestScoped
public class HomeBean {

    @Inject
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
