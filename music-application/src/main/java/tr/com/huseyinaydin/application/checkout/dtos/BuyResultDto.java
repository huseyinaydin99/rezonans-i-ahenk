package tr.com.huseyinaydin.application.checkout.dtos;

public class BuyResultDto {
    private boolean success;
    private String message;
    private String transactionId;

    public BuyResultDto(boolean success, String message, String transactionId) {
        this.success = success;
        this.message = message;
        this.transactionId = transactionId;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getTransactionId() { return transactionId; }
}
