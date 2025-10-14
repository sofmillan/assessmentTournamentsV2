package co.com.assessment.model.exception;

import lombok.Getter;

@Getter
public class SecurityException extends RuntimeException{
    private final SecurityErrorMessage securityErrorMessage;

    public SecurityException(SecurityErrorMessage securityErrorMessage) {
        super(securityErrorMessage.getMessage());
        this.securityErrorMessage = securityErrorMessage;
    }
}
