package co.com.assessment.api.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TournamentRsDto {
    private Long id;
    private String name;
    private Integer remainingCapacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double ticketPrice;
    private Boolean isFree;
}
