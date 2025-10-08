package co.com.assessment.model.tournament;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Tournament {
    private Long id;
    private String description;
    private String name;
    private Category category;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double ticketPrice;
    private boolean isFree;
    private String userId;
    private Integer remainingCapacity;
}
