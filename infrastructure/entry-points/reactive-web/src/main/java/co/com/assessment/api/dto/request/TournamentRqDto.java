package co.com.assessment.api.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
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
    private LocalDate startDate;
    @NotNull(message = "endDate is required")
    private LocalDate endDate;

    @NotNull(message = "ticketPrice is required")
    private Double ticketPrice;

    @NotNull(message = "isFree is required")
    private boolean isFree;

    public boolean getIsFree() {
        return isFree;
    }
}
