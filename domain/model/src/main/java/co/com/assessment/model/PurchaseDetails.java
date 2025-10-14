package co.com.assessment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PurchaseDetails {
    private String currency;
    private Integer tournamentId;
    private Double amount;
    private String paymentMethod;
}
