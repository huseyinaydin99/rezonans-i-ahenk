package tr.com.huseyinaydin.application.checkout.services;

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

    private final IDomainEventDispatcher eventDispatcher;

    public CheckoutServiceImpl(IDomainEventDispatcher eventDispatcher) {
        this.eventDispatcher = eventDispatcher;
    }

    @Override
    @Transactional
    public BuyResultDto processPayment(CheckoutRequestDto request) {
        System.out.println("[ÖDEME AĞ GEÇİDİ] Kullanıcı (" + request.getUserId() + ") için kredi kartı ödemesi başlatıldı...");
        
        // Simüle Edilmiş Ödeme Doğrulaması (Payment Gateway - Stripe/Iyzico vb.)
        if (request.getCreditCardNumber() == null || request.getCreditCardNumber().length() < 16) {
            return new BuyResultDto(false, "Geçersiz Kredi Kartı numarası!", null);
        }

        System.out.println("-> Kredi kartından ücret başarıyla çekildi (Simülasyon).");
        
        // 1. Kullanıcının Tier (Paket) bilgisini veritabanında güncelle
        System.out.println("-> Kullanıcı profili " + request.getTargetTier().name() + " paketine yükseltiliyor.");
        // userRepository.updateTier(...)
        
        // 2. Saga/Outbox Pattern: Fatura servisi ve bildirimler için Event fırlat
        // eventDispatcher.dispatch(new MembershipUpgradedEvent(request.getUserId(), request.getTargetTier()));
        System.out.println("-> İşlem tamamlandı. Saga Pattern (Outbox) üzerinden 'Üyelik Yükseltildi' olayı (Event) fırlatıldı.");

        String transactionId = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new BuyResultDto(true, "Ödemeniz başarıyla alındı. Aramıza hoş geldiniz!", transactionId);
    }
}
