package tr.com.huseyinaydin.web.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import tr.com.huseyinaydin.sharedkernel.exception.AuthorizationException;
import tr.com.huseyinaydin.sharedkernel.exception.MusicErrorTypes;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessException;
import tr.com.huseyinaydin.sharedkernel.exception.BusinessProblemDetail;
import tr.com.huseyinaydin.sharedkernel.exception.ConflictException;
import tr.com.huseyinaydin.sharedkernel.exception.NotFoundException;
import tr.com.huseyinaydin.sharedkernel.exception.ProblemDetail;
import tr.com.huseyinaydin.sharedkernel.exception.ValidationException;
import tr.com.huseyinaydin.sharedkernel.exception.ValidationProblemDetail;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RFC 7807 Problem Details uyumlu merkezi API Hata Yöneticisi (Global Exception Handler).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ProblemDetail> handleBusiness(BusinessException ex,
                                                        HttpServletRequest request) {
        log.warn("İş kuralı ihlali [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.BUSINESS_RULE_VIOLATION, ex.getMessage(),
                ex.getErrorCode(), request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetail> handleValidation(ValidationException ex,
                                                          HttpServletRequest request) {
        log.warn("Model doğrulama hatası [{}]: {}", request.getRequestURI(), ex.getMessage());
        MusicErrorTypes type = MusicErrorTypes.VALIDATION_FAILED;

        Map<String, List<String>> errors = ex.getErrors().stream()
                .collect(Collectors.groupingBy(
                        e -> e.field(),
                        Collectors.mapping(e -> e.message(), Collectors.toList())));

        ValidationProblemDetail body = new ValidationProblemDetail(
                type.type(), type.title(), type.status(), ex.getMessage(),
                instance(request), errors, resolveTraceId(request));

        return problem(type.status(), body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                      HttpServletRequest request) {
        log.warn("Argüman doğrulama hatası [{}]: {}", request.getRequestURI(), ex.getMessage());
        MusicErrorTypes type = MusicErrorTypes.VALIDATION_FAILED;

        Map<String, List<String>> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())));

        ValidationProblemDetail body = new ValidationProblemDetail(
                type.type(), type.title(), type.status(), "Gönderilen veride doğrulama hataları tespit edildi.",
                instance(request), errors, resolveTraceId(request));

        return problem(type.status(), body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest request) {
        log.warn("Kısıt doğrulama hatası [{}]: {}", request.getRequestURI(), ex.getMessage());
        MusicErrorTypes type = MusicErrorTypes.VALIDATION_FAILED;

        Map<String, List<String>> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String property = violation.getPropertyPath().toString();
            errors.computeIfAbsent(property, k -> new ArrayList<>()).add(violation.getMessage());
        });

        ValidationProblemDetail body = new ValidationProblemDetail(
                type.type(), type.title(), type.status(), "Alan kısıtları ihlal edildi.",
                instance(request), errors, resolveTraceId(request));

        return problem(type.status(), body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(NotFoundException ex,
                                                        HttpServletRequest request) {
        log.warn("Kaynak bulunamadı [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.NOT_FOUND, ex.getMessage(),
                ex.getErrorCode(), request);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ProblemDetail> handleAuthorization(AuthorizationException ex,
                                                             HttpServletRequest request) {
        log.warn("Yetkisiz erişim [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.UNAUTHORIZED, ex.getMessage(),
                ex.getErrorCode(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ProblemDetail> handleAuthentication(AuthenticationException ex,
                                                              HttpServletRequest request) {
        log.warn("Kimlik doğrulama hatası [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.UNAUTHORIZED, "Kimlik doğrulaması gereklidir: " + ex.getMessage(),
                "UNAUTHENTICATED", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ProblemDetail> handleAccessDenied(AccessDeniedException ex,
                                                            HttpServletRequest request) {
        log.warn("Erişim reddedildi [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.FORBIDDEN, "Bu kaynağa erişim yetkiniz bulunmamaktadır.",
                "ACCESS_DENIED", request);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ProblemDetail> handleConflict(ConflictException ex,
                                                        HttpServletRequest request) {
        log.warn("Veri çakışması [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.CONFLICT, ex.getMessage(),
                ex.getErrorCode(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex,
                                                               HttpServletRequest request) {
        log.warn("Geçersiz argüman veya domain kural ihlali [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.BUSINESS_RULE_VIOLATION, ex.getMessage(),
                "DOMAIN_RULE_VIOLATION", request);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ProblemDetail> handleIllegalState(IllegalStateException ex,
                                                            HttpServletRequest request) {
        log.warn("Geçersiz durum hatası [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.BUSINESS_RULE_VIOLATION, ex.getMessage(),
                "ILLEGAL_STATE", request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest request) {
        log.warn("Okunamayan istek gövdesi [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.BAD_REQUEST, "İstek gövdesi (JSON) biçimsel olarak hatalı veya okunamıyor.",
                "MALFORMED_REQUEST_BODY", request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ProblemDetail> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                  HttpServletRequest request) {
        log.warn("Desteklenmeyen HTTP metodu [{}]: {}", request.getRequestURI(), ex.getMessage());
        return business(MusicErrorTypes.METHOD_NOT_ALLOWED, ex.getMessage(),
                "METHOD_NOT_ALLOWED", request);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ProblemDetail> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest request) {
        log.warn("Eksik sorgu parametresi [{}]: {}", request.getRequestURI(), ex.getParameterName());
        return business(MusicErrorTypes.BAD_REQUEST, "Zorunlu sorgu parametresi eksik: " + ex.getParameterName(),
                "MISSING_PARAMETER", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        log.warn("Parametre tip uyumsuzluğu [{}]: {}", request.getRequestURI(), ex.getName());
        return business(MusicErrorTypes.BAD_REQUEST, "Geçersiz parametre değeri: " + ex.getName(),
                "TYPE_MISMATCH", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGeneral(Exception ex,
                                                       HttpServletRequest request) {
        log.error("Beklenmeyen sistem hatası [{}]: {}", request.getRequestURI(), ex.getMessage(), ex);
        return business(MusicErrorTypes.INTERNAL_ERROR, "Sunucuda beklenmeyen bir hata oluştu.",
                "INTERNAL_SERVER_ERROR", request);
    }

    private static ResponseEntity<ProblemDetail> business(MusicErrorTypes type,
                                                          String detail,
                                                          String errorCode,
                                                          HttpServletRequest request) {
        BusinessProblemDetail body = new BusinessProblemDetail(
                type.type(), type.title(), type.status(), detail,
                instance(request), errorCode, resolveTraceId(request));
        return problem(type.status(), body);
    }

    private static ResponseEntity<ProblemDetail> problem(int status, ProblemDetail body) {
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .body(body);
    }

    private static URI instance(HttpServletRequest request) {
        return URI.create(request.getRequestURI());
    }

    private static String resolveTraceId(HttpServletRequest request) {
        String traceId = request.getHeader("X-Trace-Id");
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        traceId = MDC.get("correlationId");
        if (traceId != null && !traceId.isBlank()) {
            return traceId;
        }
        return MDC.get("traceId");
    }
}
