package tr.com.huseyinaydin.sharedkernel.exception;

import java.net.URI;


public enum MusicErrorTypes {

    BUSINESS_RULE_VIOLATION("/problems/business-rule-violation", "İş Kuralı İhlali", 400),
    VALIDATION_FAILED("/problems/validation-failed", "Doğrulama Hatası", 400),
    NOT_FOUND("/problems/not-found", "Kaynak Bulunamadı", 404),
    UNAUTHORIZED("/problems/unauthorized", "Yetkisiz İşlem", 401),
    FORBIDDEN("/problems/forbidden", "Erişim Reddedildi", 403),
    CONFLICT("/problems/conflict", "Kaynak Çakışması", 409),
    BAD_REQUEST("/problems/bad-request", "Geçersiz İstek", 400),
    METHOD_NOT_ALLOWED("/problems/method-not-allowed", "Desteklenmeyen Metot", 405),
    INTERNAL_ERROR("/problems/internal-error", "Sunucu Hatası", 500);

    private final String type;
    private final String title;
    private final int status;

    MusicErrorTypes(String type, String title, int status) {
        this.type = type;
        this.title = title;
        this.status = status;
    }

    public URI type() { return URI.create(type); }

    public String title() { return title; }

    public int status() { return status; }
}
