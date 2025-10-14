package co.com.assessment.model.exception;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException{
    private final TechnicalErrorMessage technicalErrorMessage;

    public TechnicalException(TechnicalErrorMessage technicalErrorMessage) {
        super(technicalErrorMessage.getMessage());
        this.technicalErrorMessage = technicalErrorMessage;
    }
}
