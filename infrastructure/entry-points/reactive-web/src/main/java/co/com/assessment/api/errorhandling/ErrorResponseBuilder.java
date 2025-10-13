package co.com.assessment.api.errorhandling;

import co.com.assessment.api.validation.ObjectValidationException;
import co.com.assessment.model.tournament.exception.BusinessException;
import co.com.assessment.model.tournament.exception.SecurityException;
import co.com.assessment.model.tournament.exception.TechnicalException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ErrorResponseBuilder {

    private record ErrorMetadata(String title, Integer code, String detail){}
    private record ErrorContext(ServerRequest serverRequest, Throwable throwable){}
    public Mono<ErrorModel> buildErrorResponse(BusinessException ex, ServerRequest request){
        var metadata = new ErrorMetadata(ex.getBusinessErrorMessage().getStatus(),
                ex.getBusinessErrorMessage().getStatusCode(),
                ex.getBusinessErrorMessage().getMessage());
        var context = new ErrorContext(request, ex);
        return buildErrorResponse(metadata, context);
    }

    public Mono<ErrorModel> buildErrorResponse(SecurityException ex, ServerRequest request){
        var metadata = new ErrorMetadata(ex.getSecurityErrorMessage().getStatus(),
                ex.getSecurityErrorMessage().getStatusCode(),
                ex.getSecurityErrorMessage().getMessage());
        var context = new ErrorContext(request, ex);
        return buildErrorResponse(metadata, context);
    }

    public Mono<ErrorModel> buildErrorResponse(TechnicalException ex, ServerRequest request){
        var metadata = new ErrorMetadata(ex.getTechnicalErrorMessage().getStatus(),
                ex.getTechnicalErrorMessage().getStatusCode(),
                ex.getTechnicalErrorMessage().getMessage());
        var context = new ErrorContext(request, ex);
        return buildErrorResponse(metadata, context);
    }

    public Mono<ErrorModel> buildErrorResponse(ObjectValidationException ex, ServerRequest request){
        var metadata = new ErrorMetadata("Bad request",
                400,
                "");
        var context = new ErrorContext(request, ex);
        return buildErrorResponse(metadata, context);
    }

    private Mono<ErrorModel> buildErrorResponse(ErrorMetadata errorMetadata, ErrorContext context){
        List<String> details;
        if(context.throwable() instanceof ObjectValidationException ex){
            details = ex.getDetails();
        }else{
            details = List.of(errorMetadata.detail);
        }

        var response = ErrorModel.builder()
                .errorMessages(details)
                .status(errorMetadata.title())
                .dateTime(LocalDateTime.now())
                .statusCode(errorMetadata.code())
                .build();

        return Mono.just(response);
    }

    public Mono<ServerResponse> buildFinalResponse(ErrorModel errorModel){
        HttpStatus status = Optional.of(HttpStatus.resolve(errorModel.getStatusCode())).orElse(HttpStatus.BAD_REQUEST);

        return ServerResponse.status(status)
                .bodyValue(errorModel);
    }
}
