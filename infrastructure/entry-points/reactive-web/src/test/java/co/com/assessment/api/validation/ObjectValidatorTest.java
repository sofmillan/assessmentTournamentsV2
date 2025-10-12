package co.com.assessment.api.validation;

import co.com.assessment.api.dto.request.TournamentRqDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectValidatorTest {
    private ObjectValidator objectValidator;
    @BeforeEach
    void setUp() {
        objectValidator = new ObjectValidator();
    }

    @Test
    void validateShouldNotThrowExceptionWhenObjectIsValid() {
        TournamentRqDto validTournament = TournamentRqDto.builder()
                .categoryId(1)
                .name("Random name")
                .description("Random description")
                .free(true)
                .ticketPrice(0.0)
                .startDate(LocalDate.of(2025,11,10))
                .endDate(LocalDate.of(2025,11,17))
                .build();

        assertDoesNotThrow(() -> objectValidator.validate(validTournament));
    }

    @Test
    void validateShouldThrowObjectValidationExceptionWhenRequiredFieldNotPresent() {
        TournamentRqDto tournamentWithoutPrice = TournamentRqDto.builder()
                .categoryId(1)
                .name("Random name")
                .description("Random description")
                .free(true)
                .startDate(LocalDate.of(2025,11,10))
                .endDate(LocalDate.of(2025,11,17))
                .build();


        ObjectValidationException ex = assertThrows(
                ObjectValidationException.class,
                () -> objectValidator.validate(tournamentWithoutPrice)
        );

        assertTrue(ex.getDetails().contains("ticketPrice is required"));
    }

    @Test
    void validateShouldCollectMultipleErrorMessagesWhenMultipleViolations() {
        TournamentRqDto invalidTournament = TournamentRqDto.builder()
                .description("Random description")
                .free(true)
                .ticketPrice(0.0)
                .endDate(LocalDate.of(2025,11,17))
                .build();

        ObjectValidationException ex = assertThrows(
                ObjectValidationException.class,
                () -> objectValidator.validate(invalidTournament)
        );

        assertFalse(ex.getDetails().isEmpty());
        assertTrue( ex.getDetails().size()>1);
    }
}
