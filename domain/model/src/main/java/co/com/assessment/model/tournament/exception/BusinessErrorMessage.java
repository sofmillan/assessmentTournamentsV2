package co.com.assessment.model.tournament.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {

    INVALID_REQUEST(400, "BAD REQUEST", "Body cannot be empty");
    private final Integer statusCode;
    private final String title;
    private final String message;
}
