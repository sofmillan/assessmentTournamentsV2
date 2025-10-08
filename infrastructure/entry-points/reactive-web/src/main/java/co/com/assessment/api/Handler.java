package co.com.assessment.api;

import co.com.assessment.api.dto.request.TournamentRqDto;
import co.com.assessment.api.dto.response.TournamentRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.exception.BusinessErrorMessage;
import co.com.assessment.model.tournament.exception.BusinessException;
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

    public Mono<ServerResponse> listenPOSTCreateTournament(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(TournamentRqDto.class)
                .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.INVALID_REQUEST)))
                .doOnNext(objectValidator::validate)
                .map(dto -> objectMapper.map(dto, Tournament.class))
                .flatMap(tournamentsUseCase::createTournament)
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .flatMap(this::buildResponse);

    }

    public Mono<ServerResponse> listenGETOtherUseCase(ServerRequest serverRequest) {
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return tournamentsUseCase.getTournamentById(id)
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenPOSTUseCase(ServerRequest serverRequest) {
        // useCase.logic();
        return ServerResponse.ok().bodyValue("");
    }

    private Mono<ServerResponse> buildResponse(Object userSignupRqDto){
        return ServerResponse.ok().bodyValue(userSignupRqDto);
    }
}
