package tr.com.huseyinaydin.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import tr.com.huseyinaydin.sharedkernel.logging.CorrelationId;

import java.io.IOException;
import java.util.UUID;

/**
 * Gelen her HTTP isteğine benzersiz Correlation ID ve Dağıtık Trace ID atayan,
 * bu bilgileri MDC üzerinden loglara (JSON/Konsol) basan ve yanıt başlıklarına ekleyen filtre.
 */
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String CORRELATION_HEADER = "X-Correlation-ID";
    public static final String TRACE_HEADER = "X-Trace-Id";
    public static final String MDC_CORRELATION_KEY = "correlationId";
    public static final String MDC_TRACE_KEY = "traceId";
    public static final String MDC_SPAN_KEY = "spanId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CorrelationId correlationId = CorrelationId.of(request.getHeader(CORRELATION_HEADER));
        String cidValue = correlationId.asString();

        String incomingTraceId = request.getHeader(TRACE_HEADER);
        if (incomingTraceId == null || incomingTraceId.isBlank()) {
            incomingTraceId = cidValue.replace("-", "");
        }
        String spanId = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        MDC.put(MDC_CORRELATION_KEY, cidValue);
        MDC.put(MDC_TRACE_KEY, incomingTraceId);
        MDC.put(MDC_SPAN_KEY, spanId);

        response.setHeader(CORRELATION_HEADER, cidValue);
        response.setHeader(TRACE_HEADER, incomingTraceId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_CORRELATION_KEY);
            MDC.remove(MDC_TRACE_KEY);
            MDC.remove(MDC_SPAN_KEY);
        }
    }
}
