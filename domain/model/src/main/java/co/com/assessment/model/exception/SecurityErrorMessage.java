package co.com.assessment.model.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SecurityErrorMessage {
    INVALID_CREDENTIALS(401, "Unauthorized","Invalid token");
    private final Integer statusCode;
    private final String status;
    private final String message;
}
