package co.com.assessment.model.tournament.gateways;

import co.com.assessment.model.tournament.Confirmation;
import co.com.assessment.model.tournament.PurchaseDetails;
import reactor.core.publisher.Mono;

public interface PaymentGateway {

    Mono<Confirmation> processPayment(PurchaseDetails purchaseDetails);
}
