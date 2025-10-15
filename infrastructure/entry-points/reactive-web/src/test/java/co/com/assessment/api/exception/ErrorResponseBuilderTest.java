package co.com.assessment.api.exception;

import co.com.assessment.api.errorhandling.ErrorResponseBuilder;
import co.com.assessment.model.exception.*;
import co.com.assessment.model.exception.SecurityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorResponseBuilderTest {
    private ErrorResponseBuilder errorResponseBuilder;

    @BeforeEach
    void setUp() {
        errorResponseBuilder = new ErrorResponseBuilder();
    }

    @Test
    void buildErrorResponseShouldReturnErrorModelForBusinessException() {
        BusinessErrorMessage businessErrorMessage = BusinessErrorMessage.INVALID_REQUEST;
        BusinessException ex = new BusinessException(businessErrorMessage);
        ServerRequest request = MockServerRequest.builder().build();

        errorResponseBuilder.buildErrorResponse(ex, request)
                .as(StepVerifier::create)
                .assertNext(errorModel -> {
                    assertEquals(businessErrorMessage.getMessage(), errorModel.getErrorMessages().get(0));
                    assertEquals(businessErrorMessage.getStatus(), errorModel.getStatus());
                    assertEquals(businessErrorMessage.getStatusCode(), errorModel.getStatusCode());
                    assertNotNull(errorModel.getDateTime());
                })
                .verifyComplete();
    }

    @Test
    void buildErrorResponseShouldReturnErrorModelForSecurityException() {
        SecurityErrorMessage securityExceptionMessage = SecurityErrorMessage.INVALID_CREDENTIALS;
        SecurityException ex = new SecurityException(securityExceptionMessage);
        ServerRequest request = MockServerRequest.builder().build();

        errorResponseBuilder.buildErrorResponse(ex, request)
                .as(StepVerifier::create)
                .assertNext(errorModel -> {
                    assertEquals(securityExceptionMessage.getMessage(), errorModel.getErrorMessages().get(0));
                    assertEquals(securityExceptionMessage.getStatus(), errorModel.getStatus());
                    assertEquals(securityExceptionMessage.getStatusCode(), errorModel.getStatusCode());
                    assertNotNull(errorModel.getDateTime());
                })
                .verifyComplete();
    }

    @Test
    void buildErrorResponseShouldReturnErrorModelForTechnicalException() {
        TechnicalErrorMessage technicalErrorMessage = TechnicalErrorMessage.TECHNICAL_ERROR;
        TechnicalException ex = new TechnicalException(technicalErrorMessage);
        ServerRequest request = MockServerRequest.builder().build();

        errorResponseBuilder.buildErrorResponse(ex, request)
                .as(StepVerifier::create)
                .assertNext(errorModel -> {
                    assertEquals(technicalErrorMessage.getMessage(), errorModel.getErrorMessages().get(0));
                    assertEquals(technicalErrorMessage.getStatus(), errorModel.getStatus());
                    assertEquals(technicalErrorMessage.getStatusCode(), errorModel.getStatusCode());
                    assertNotNull(errorModel.getDateTime());
                })
                .verifyComplete();
    }


}

