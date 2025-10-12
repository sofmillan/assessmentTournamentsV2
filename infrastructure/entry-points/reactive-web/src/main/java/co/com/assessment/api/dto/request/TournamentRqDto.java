package co.com.assessment.api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentRqDto {
    @NotNull(message = "name is required")
    @Size(min = 1, max=50, message = "name must be between 1 and 50 characters long")
    private String name;

    @NotNull(message = "description is required")
    @Size(min = 1, max=100, message = "description must be between 1 and 100 characters long")
    private String description;

    @NotNull(message = "categoryId is required")
    private Long categoryId;
    @NotNull(message = "startDate is required")
    @Future(message = "startDate must be in the future")
    private LocalDate startDate;

    @Future(message = "endDate must be in the future")
    @NotNull(message = "endDate is required")
    private LocalDate endDate;

    @NotNull(message = "ticketPrice is required")
    private Double ticketPrice;

    @NotNull(message = "free is required")
    private boolean free;
}
