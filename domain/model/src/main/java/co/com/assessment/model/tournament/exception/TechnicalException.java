package co.com.assessment.model.tournament.exception;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException{
    private final TechnicalErrorMessage technicalErrorMessage;

    public TechnicalException(TechnicalErrorMessage technicalErrorMessage) {
        this.technicalErrorMessage = technicalErrorMessage;
    }
}
