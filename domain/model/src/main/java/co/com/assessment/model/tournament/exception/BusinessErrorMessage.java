package co.com.assessment.model.tournament.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {

    INVALID_CREDENTIALS(401, "Unauthorized","Invalid credentials");
    private final Integer statusCode;
    private final String title;
    private final String message;
}
