package co.com.assessment.model.gateways;

import co.com.assessment.model.Confirmation;
import co.com.assessment.model.PurchaseDetails;
import reactor.core.publisher.Mono;

public interface PaymentGateway {

    Mono<Confirmation> processPayment(PurchaseDetails purchaseDetails);
}
