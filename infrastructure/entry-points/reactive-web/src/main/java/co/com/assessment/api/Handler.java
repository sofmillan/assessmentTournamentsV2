package co.com.assessment.api;

import co.com.assessment.api.dto.request.TournamentRqDto;
import co.com.assessment.api.dto.response.TournamentListRsDto;
import co.com.assessment.api.dto.response.TournamentRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.exception.BusinessErrorMessage;
import co.com.assessment.model.tournament.exception.BusinessException;
import co.com.assessment.model.tournament.exception.SecurityErrorMessage;
import co.com.assessment.model.tournament.exception.SecurityException;
import co.com.assessment.tokenresolver.JwtResolver;
import co.com.assessment.usecase.tournaments.TournamentsUseCase;
import org.reactivecommons.utils.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Handler {
    private final ObjectValidator objectValidator;
    private final TournamentsUseCase tournamentsUseCase;
    private final ObjectMapper objectMapper;
    private final JwtResolver jwtResolver;


    public Mono<ServerResponse> listenPOSTCreateTournament(ServerRequest serverRequest) {

        return validateToken(serverRequest)
                .flatMap(s -> s.bodyToMono(TournamentRqDto.class))
                .switchIfEmpty(Mono.error(() -> new BusinessException(BusinessErrorMessage.INVALID_REQUEST)))
                .doOnNext(objectValidator::validate)
                .map(dto -> {
                    String userId = this.getUser(serverRequest);
                    var model = objectMapper.map(dto, Tournament.class);
                    model.setUserId(userId);
                    return model;
                })
                .flatMap(tournamentsUseCase::createTournament)
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .flatMap(this::buildResponse);

    }

    public Mono<ServerResponse> listenGETFindTournamentById(ServerRequest serverRequest) {
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return this.validateToken(serverRequest)
                .flatMap(validatedRequest -> {
                    return tournamentsUseCase.getTournamentById(id);
                })
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenGETAllTournaments(ServerRequest serverRequest) {
        boolean createdByMe = serverRequest.queryParam("createdByMe")
                .map(Boolean::parseBoolean)
                .orElse(false);
        return validateToken(serverRequest)
                .flatMapMany(validatedRequest -> {
                    if (createdByMe) {
                        String userId = this.getUser(validatedRequest);
                        return tournamentsUseCase.getTournamentsByUser(userId);
                    } else {
                        return tournamentsUseCase.getAllTournaments();
                    }})
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .collectList()
                .map(a -> TournamentListRsDto.builder().tournaments(a))
                .flatMap(this::buildResponse);
    }



    public Mono<ServerRequest> validateToken(ServerRequest serverRequest) {

        final String BEARER_PREFIX = "Bearer ";

        // 1. Safely extract the token
        String authHeader = serverRequest.headers().firstHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            // If the header is missing or malformed, immediately return an error
            return Mono.error(() -> new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS));
        }

        String token = authHeader.substring(BEARER_PREFIX.length());
        System.out.println(token);
        // 2. Wrap the synchronous validation in a Mono
        return Mono.fromCallable(() -> {
                    // Synchronous call to your resolver: throws exception (e.g., JwtValidationException) if invalid
                    jwtResolver.validate(token);

                    // If validation succeeds, return the original ServerRequest object
                    return serverRequest;
                })
                // 3. Map any validation exception to your BusinessException
                .onErrorMap(e -> new SecurityException(SecurityErrorMessage.INVALID_CREDENTIALS));
    }

    public String getUser(ServerRequest serverRequest){
        return this.jwtResolver.validateAndExtractSub(serverRequest.headers().firstHeader("Authorization"));
    }
    private Mono<ServerResponse> buildResponse(Object userSignupRqDto) {
        return ServerResponse.ok().bodyValue(userSignupRqDto);
    }
}
