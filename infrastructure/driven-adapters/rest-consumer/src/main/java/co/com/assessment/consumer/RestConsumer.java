package co.com.assessment.consumer;

import co.com.assessment.model.tournament.Confirmation;
import co.com.assessment.model.tournament.PurchaseDetails;
import co.com.assessment.model.tournament.gateways.PaymentGateway;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class RestConsumer implements PaymentGateway{
    private final WebClient client;
    private final ObjectMapper mapper;

    public RestConsumer(ObjectMapper mapper) {
        this.client = WebClient.create("https://4d1d4fa3-b992-459f-97ec-78a6c0baad33.mock.pstmn.io");
        this.mapper = mapper;
    }

    @Override
    public Mono<Confirmation> processPayment(PurchaseDetails purchaseDetails) {
        ObjectRequest request = mapper.map(purchaseDetails, ObjectRequest.class);
        Mono<ObjectResponse> response = client.post()
                .uri("/pay")
                .header("x-api-key", "")
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);


        return response.flatMap(r -> Mono.just(Confirmation.builder()
                    .status(r.getStatus())
                    .transactionDetails(Confirmation.TransactionDetails.builder()
                            .transactionId(r.getTransactionDetails().getTransactionId())
                            .paymentMethod(r.getTransactionDetails().getPaymentMethod())
                            .amountPaid(r.getTransactionDetails().getAmountPaid())
                            .build())

                    .build()));
    }
}
