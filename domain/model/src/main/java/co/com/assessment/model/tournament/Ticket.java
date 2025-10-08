package co.com.assessment.model.tournament;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
//@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Ticket {
    private Long id;
    private String userId;
    private Tournament tournament;
    private Double totalPrice;
    private LocalDateTime purchaseDate;
    private String code;
}
