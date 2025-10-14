package co.com.assessment.model.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{
    private final BusinessErrorMessage businessErrorMessage;

    public BusinessException(BusinessErrorMessage businessErrorMessage) {
        super(businessErrorMessage.getMessage());
        this.businessErrorMessage = businessErrorMessage;
    }
}
