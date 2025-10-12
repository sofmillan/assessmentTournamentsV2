package co.com.assessment.api.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class DetailedTournamentRsDto {
    private Long id;
    private Integer categoryId;
    private String name;
    private String description;
    private Integer remainingCapacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double ticketPrice;
    private Boolean free;
}
