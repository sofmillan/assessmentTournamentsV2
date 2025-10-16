package co.com.assessment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentMetrics {
    private Double revenue;
    private Integer numberSoldTickets;
    private Integer remainingCapacity;
    private Integer totalCapacity;

}
