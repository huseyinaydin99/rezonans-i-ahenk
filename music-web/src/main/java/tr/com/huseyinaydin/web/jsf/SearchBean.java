package tr.com.huseyinaydin.web.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import org.springframework.beans.factory.annotation.Autowired;
import tr.com.huseyinaydin.application.cqrs.Mediator;
import tr.com.huseyinaydin.application.search.queries.GlobalSearchQuery;
import tr.com.huseyinaydin.application.search.queries.GlobalSearchResponse;

@Named
@RequestScoped
public class SearchBean {

    @Autowired
    private Mediator mediator;

    private String query;
    private GlobalSearchResponse searchResults;
    private boolean hasSearched = false;

    public void executeSearch() {
        if (query != null && !query.trim().isEmpty()) {
            this.searchResults = mediator.send(new GlobalSearchQuery(query));
            this.hasSearched = true;
        }
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public GlobalSearchResponse getSearchResults() { return searchResults; }
    public boolean isHasSearched() { return hasSearched; }
}
