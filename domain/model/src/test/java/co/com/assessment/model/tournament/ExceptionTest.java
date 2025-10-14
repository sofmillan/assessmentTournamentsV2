package co.com.assessment.model.tournament;


import co.com.assessment.model.exception.*;
import co.com.assessment.model.exception.SecurityException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class ExceptionTest {

    @Test
    void businessExceptionShouldStoreMessage() {
        BusinessException ex = new BusinessException(BusinessErrorMessage.TOURNAMENT_SOLD_OUT);

        assertEquals(BusinessErrorMessage.TOURNAMENT_SOLD_OUT.getMessage(), ex.getMessage());
        assertEquals(BusinessErrorMessage.TOURNAMENT_SOLD_OUT, ex.getBusinessErrorMessage());
    }

    @Test
    void securityExceptionShouldStoreMessage() {
        SecurityException ex = new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS);

        assertEquals(SecurityErrorMessage.INVALID_CREDENTIALS.getMessage(), ex.getMessage());
        assertEquals(SecurityErrorMessage.INVALID_CREDENTIALS, ex.getSecurityErrorMessage());
    }

    @Test
    void technicalExceptionShouldStoreMessage() {
        TechnicalException ex = new TechnicalException(TechnicalErrorMessage.TECHNICAL_ERROR);

        assertEquals(TechnicalErrorMessage.TECHNICAL_ERROR.getMessage(), ex.getMessage());
        assertEquals(TechnicalErrorMessage.TECHNICAL_ERROR, ex.getTechnicalErrorMessage());
    }
}
