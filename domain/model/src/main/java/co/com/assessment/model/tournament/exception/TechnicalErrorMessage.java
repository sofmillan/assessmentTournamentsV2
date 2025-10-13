package co.com.assessment.model.tournament.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TechnicalErrorMessage {
    TECHNICAL_ERROR(500, "Internal Server Error", "An internal error ocurred");

    private final Integer statusCode;
    private final String status;
    private final String message;

}
