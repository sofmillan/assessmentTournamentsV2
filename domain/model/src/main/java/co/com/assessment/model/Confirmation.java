package co.com.assessment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Confirmation {

    private String status;
    private TransactionDetails transactionDetails;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    public static class TransactionDetails{
        private String transactionId;
        private Double amountPaid;
        private String paymentMethod;
    }
}
