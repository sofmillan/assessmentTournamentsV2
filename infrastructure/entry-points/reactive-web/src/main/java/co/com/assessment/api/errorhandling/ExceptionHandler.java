package co.com.assessment.api.errorhandling;

import co.com.assessment.api.validation.ObjectValidationException;
import co.com.assessment.model.tournament.exception.BusinessException;
import co.com.assessment.model.tournament.exception.SecurityException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Order(-2)
@Component
public class ExceptionHandler extends AbstractErrorWebExceptionHandler {
    /**
     * Create a new {@code AbstractErrorWebExceptionHandler}.
     *
     * @param errorAttributes    the error attributes
     * @param resources          the resources configuration properties
     * @param applicationContext the application context
     * @since 2.4.0
     */
    private final ErrorResponseBuilder errorResponseBuilder;
    public ExceptionHandler(
            ErrorAttributes errorAttributes,
            WebProperties.Resources resources,
            ApplicationContext applicationContext,
            ErrorResponseBuilder errorResponseBuilder,
            ServerCodecConfigurer server) {
        super(errorAttributes, resources, applicationContext);
        this.setMessageWriters(server.getWriters());
        this.errorResponseBuilder = errorResponseBuilder;
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    Mono<ServerResponse> renderErrorResponse(ServerRequest serverRequest){
        return Mono.error(getError(serverRequest))
                .onErrorResume(SecurityException.class, ex ->
                        errorResponseBuilder.buildErrorResponse(ex, serverRequest))
                .onErrorResume(BusinessException.class, ex ->
                        errorResponseBuilder.buildErrorResponse(ex, serverRequest))
                .onErrorResume(ObjectValidationException.class, ex->
                        errorResponseBuilder.buildErrorResponse(ex, serverRequest))
                .cast(ErrorModel.class)
                .flatMap(errorResponseBuilder::buildfinalResponse);
    }
}
