package tr.com.huseyinaydin.application.pipeline;

public interface ICacheableRequest {
    String getCacheKey();
    default long getExpirationMinutes() {
        return 60; // Default 1 hour
    }
}
