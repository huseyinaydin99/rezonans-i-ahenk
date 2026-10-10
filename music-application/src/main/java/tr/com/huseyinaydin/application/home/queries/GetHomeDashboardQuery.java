package tr.com.huseyinaydin.application.home.queries;

import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.home.dtos.HomeDashboardDto;
import tr.com.huseyinaydin.application.ports.IHomeService;
import tr.com.huseyinaydin.sharedkernel.messaging.IQuery;
import tr.com.huseyinaydin.sharedkernel.messaging.IQueryHandler;

public record GetHomeDashboardQuery(String userId) implements IQuery<HomeDashboardDto> {
    
    @Component
    public static class Handler implements IQueryHandler<GetHomeDashboardQuery, HomeDashboardDto> {
        
        private final IHomeService homeService;

        public Handler(IHomeService homeService) {
            this.homeService = homeService;
        }

        @Override
        public HomeDashboardDto handle(GetHomeDashboardQuery query) {
            return homeService.buildHomeDashboard(query.userId());
        }
    }
}
