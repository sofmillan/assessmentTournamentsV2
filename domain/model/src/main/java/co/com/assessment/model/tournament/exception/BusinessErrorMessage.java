package co.com.assessment.model.tournament.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BusinessErrorMessage {

    INVALID_REQUEST(400, "BAD REQUEST", "Body cannot be empty"),
    CATEGORY_NOT_EXIST(404, "NOT FOUND", "Category does not exist"),
    TOURNAMENT_NOT_EXIST(404, "NOT FOUND", "Tournament does not exist");
    private final Integer statusCode;
    private final String status;
    private final String message;
}
