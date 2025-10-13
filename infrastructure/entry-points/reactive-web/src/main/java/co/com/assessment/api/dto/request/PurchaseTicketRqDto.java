package co.com.assessment.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PurchaseTicketRqDto {

    @NotNull(message = "tournamentId is required")
    private Integer tournamentId;
    private String currency;
    private String paymentMethod;
}
