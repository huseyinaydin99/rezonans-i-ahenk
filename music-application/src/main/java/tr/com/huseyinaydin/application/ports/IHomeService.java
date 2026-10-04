package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;

public interface IHomeService {
    HomeDashboardDto buildHomeDashboard(String userId);
}
