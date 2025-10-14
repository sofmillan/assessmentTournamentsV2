package co.com.assessment.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Ticket {
    private Integer id;
    private String userId;
    private Integer tournamentId;
    private Double totalPrice;
    private LocalDateTime purchaseDate;
    private String code;
    private String transactionId;
}
