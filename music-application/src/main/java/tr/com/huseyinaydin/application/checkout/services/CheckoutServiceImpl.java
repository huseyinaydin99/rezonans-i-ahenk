package tr.com.huseyinaydin.application.checkout.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tr.com.huseyinaydin.application.ports.ICheckoutService;
import tr.com.huseyinaydin.application.checkout.dtos.BuyResultDto;
import tr.com.huseyinaydin.application.checkout.dtos.CheckoutRequestDto;
import tr.com.huseyinaydin.sharedkernel.events.IDomainEventDispatcher;
// import tr.com.huseyinaydin.domain.user.events.MembershipUpgradedEvent;

import java.util.UUID;

@Service
public class CheckoutServiceImpl implements ICheckoutService {
    private static final Logger log = LoggerFactory.getLogger(CheckoutServiceImpl.class);


    private final IDomainEventDispatcher eventDispatcher;

    public CheckoutServiceImpl(IDomainEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    @Transactional
    public BuyResultDto processPayment(CheckoutRequestDto request) {
        log.info("[ÖDEME AĞ GEÇİDİ] Kullanıcı (" + request.getUserId() + ") için kredi kartı ödemesi başlatıldı...");
        
        // Simüle Edilmiş Ödeme Doğrulaması (Payment Gateway - Stripe/Iyzico vb.)
        if (request.getCreditCardNumber() == null || request.getCreditCardNumber().length() < 16) {
            return new BuyResultDto(false, "Geçersiz Kredi Kartı numarası!", null);
        }

        log.info("-> Kredi kartından ücret başarıyla çekildi (Simülasyon).");
        
        // 1. Kullanıcının Tier (Paket) bilgisini veritabanında güncelle
        log.info("-> Kullanıcı profili " + request.getTargetTier().name() + " paketine yükseltiliyor.");
        // userRepository.updateTier(...)
        
        // 2. Saga/Outbox Pattern: Fatura servisi ve bildirimler için Event fırlat
        // eventDispatcher.dispatch(new MembershipUpgradedEvent(request.getUserId(), request.getTargetTier()));
        log.info("-> İşlem tamamlandı. Saga Pattern (Outbox) üzerinden 'Üyelik Yükseltildi' olayı (Event) fırlatıldı.");

        String transactionId = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new BuyResultDto(true, "Ödemeniz başarıyla alındı. Aramıza hoş geldiniz!", transactionId);
    }
}
