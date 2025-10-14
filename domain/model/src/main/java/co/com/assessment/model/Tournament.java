package co.com.assessment.model;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Tournament {
    private Integer id;
    private String description;
    private String name;
    private Integer categoryId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double ticketPrice;
    private boolean free;
    private String userId;
    private Integer remainingCapacity;
}
