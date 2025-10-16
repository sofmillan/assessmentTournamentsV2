package co.com.assessment.api.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentMetricsRsDto {
    private Double revenue;
    private Integer numberSoldTickets;
    private Integer remainingCapacity;
    private Integer totalCapacity;
}
