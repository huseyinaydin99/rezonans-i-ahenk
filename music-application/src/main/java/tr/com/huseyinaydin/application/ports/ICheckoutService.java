package tr.com.huseyinaydin.application.ports;

import tr.com.huseyinaydin.application.checkout.dtos.BuyResultDto;
import tr.com.huseyinaydin.application.checkout.dtos.CheckoutRequestDto;

public interface ICheckoutService {
    BuyResultDto processPayment(CheckoutRequestDto request);
}
