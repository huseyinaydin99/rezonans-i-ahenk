package tr.com.huseyinaydin.application.cqrs.behaviors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import tr.com.huseyinaydin.application.cqrs.IPipelineBehavior;
import tr.com.huseyinaydin.application.cqrs.PipelineDelegate;
import tr.com.huseyinaydin.sharedkernel.messaging.ICommand;

@Component
@Order(100)
public class TransactionBehavior<TRequest, TResponse> implements IPipelineBehavior<TRequest, TResponse> {

    private static final Logger log = LoggerFactory.getLogger(TransactionBehavior.class);
    private final TransactionTemplate transactionTemplate;

    public TransactionBehavior(TransactionTemplate transactionTemplate) {
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public TResponse handle(TRequest request, PipelineDelegate<TResponse> next) {
        // Yalnızca ICommand (veri yazma/değiştirme) nesnelerine Transaction uygula
        if (request instanceof ICommand) {
            log.info("🔄 [CQRS-TRANSACTION] İşlem (Transaction) başlatıldı: {}", request.getClass().getSimpleName());
            return transactionTemplate.execute(status -> {
                try {
                    TResponse response = next.proceed();
                    log.info("✅ [CQRS-TRANSACTION] İşlem başarıyla tamamlandı, COMMIT ediliyor: {}", request.getClass().getSimpleName());
                    return response;
                } catch (Exception e) {
                    log.error("❌ [CQRS-TRANSACTION] Veritabanı işlemi sırasında hata oluştu, geri alınıyor (ROLLBACK): {}", request.getClass().getSimpleName(), e);
                    status.setRollbackOnly();
                    throw e;
                }
            });
        }
        
        // IQuery (veri okuma) gibi read-only işlemlerde Transaction'a ihtiyaç yok, doğrudan çalıştır.
        return next.proceed();
    }
}
