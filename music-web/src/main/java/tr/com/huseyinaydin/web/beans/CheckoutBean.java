package tr.com.huseyinaydin.web.beans;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tr.com.huseyinaydin.application.ports.ICheckoutService;
import tr.com.huseyinaydin.application.checkout.dtos.BuyResultDto;
import tr.com.huseyinaydin.application.checkout.dtos.CheckoutRequestDto;
import tr.com.huseyinaydin.domain.enums.MembershipTier;

@Component
@RequestScope
public class CheckoutBean {
    private static final Logger log = LoggerFactory.getLogger(CheckoutBean.class);


    @Autowired
    private ICheckoutService checkoutService;

    private String selectedTierStr;
    private String cardNumber;
    private String cardName;
    private String cvv;
    
    // İşlem Sonucu Mesajları için
    private BuyResultDto result;

    public void processOrder() {
        CheckoutRequestDto req = new CheckoutRequestDto();
        req.setUserId("current-logged-in-user");
        req.setTargetTier(MembershipTier.valueOf(selectedTierStr));
        req.setCreditCardNumber(cardNumber);
        req.setCardHolderName(cardName);
        req.setCvv(cvv);

        this.result = checkoutService.processPayment(req);
        
        if(this.result.isSuccess()) {
            log.info("[JSF AJAX] Ödeme başarılı. TRX: " + result.getTransactionId());
        } else {
            log.error("[JSF AJAX] Ödeme reddedildi: " + result.getMessage());
        }
    }

    // Getters & Setters
    public String getSelectedTierStr() { return selectedTierStr; }
    public void setSelectedTierStr(String selectedTierStr) { this.selectedTierStr = selectedTierStr; }
    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
    public String getCardName() { return cardName; }
    public void setCardName(String cardName) { this.cardName = cardName; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
    public BuyResultDto getResult() { return result; }
}
