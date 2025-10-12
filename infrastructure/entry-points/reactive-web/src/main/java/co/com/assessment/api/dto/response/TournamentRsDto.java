package co.com.assessment.api.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentRsDto {
    private Long id;
    private String name;
    private Integer remainingCapacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double ticketPrice;
}
