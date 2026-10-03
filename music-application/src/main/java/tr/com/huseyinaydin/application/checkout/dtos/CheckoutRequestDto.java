package tr.com.huseyinaydin.application.checkout.dtos;

import tr.com.huseyinaydin.domain.enums.MembershipTier;

public class CheckoutRequestDto {
    private String userId;
    private MembershipTier targetTier;
    private String creditCardNumber;
    private String cardHolderName;
    private String cvv;

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public MembershipTier getTargetTier() { return targetTier; }
    public void setTargetTier(MembershipTier targetTier) { this.targetTier = targetTier; }
    public String getCreditCardNumber() { return creditCardNumber; }
    public void setCreditCardNumber(String creditCardNumber) { this.creditCardNumber = creditCardNumber; }
    public String getCardHolderName() { return cardHolderName; }
    public void setCardHolderName(String cardHolderName) { this.cardHolderName = cardHolderName; }
    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }
}
