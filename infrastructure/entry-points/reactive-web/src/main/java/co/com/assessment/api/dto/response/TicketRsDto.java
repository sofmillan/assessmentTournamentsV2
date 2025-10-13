package co.com.assessment.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TicketRsDto {
    private Integer tournamentId;
    private Double totalPrice;
    private LocalDateTime purchaseDate;
    private String code;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String transactionId;
}
