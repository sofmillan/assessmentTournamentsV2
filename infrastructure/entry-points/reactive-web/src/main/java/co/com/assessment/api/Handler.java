package co.com.assessment.api;

import co.com.assessment.api.dto.request.TournamentRqDto;
import co.com.assessment.api.dto.response.DetailedTournamentRsDto;
import co.com.assessment.api.dto.response.TournamentListRsDto;
import co.com.assessment.api.dto.response.TournamentRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.tournament.PurchaseDetails;
import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.exception.BusinessErrorMessage;
import co.com.assessment.model.tournament.exception.BusinessException;
import co.com.assessment.tokenresolver.JwtResolver;
import co.com.assessment.usecase.tournaments.TicketsUseCase;
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

    private final TicketsUseCase ticketsUseCase;


    public Mono<ServerResponse> listenPOSTCreateTournament(ServerRequest serverRequest) {
        return serverRequest
                .bodyToMono(TournamentRqDto.class)
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
        return tournamentsUseCase.getTournamentById(id)
                .map(tournament -> objectMapper.map(tournament, DetailedTournamentRsDto.class))
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenGETAllTournaments(ServerRequest serverRequest) {
        boolean createdByMe = serverRequest.queryParam("createdByMe")
                .map(Boolean::parseBoolean)
                .orElse(false);
        return Mono.just(serverRequest)
                .flatMapMany(validatedRequest -> {
                    if (createdByMe) {
                        String userId = this.getUser(validatedRequest);
                        return tournamentsUseCase.getTournamentsByUser(userId);
                    } else {
                        return tournamentsUseCase.getAllTournaments();
                    }})
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .collectList()
                .map(tournamentList -> TournamentListRsDto.builder().tournaments(tournamentList).build())
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenGETPurchaseTicket(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(PurchaseDetails.class)
                .flatMap(details -> ticketsUseCase.purchaseTicket(details, "randomuser"))
                .flatMap(this::buildResponse);
    }

    public String getUser(ServerRequest serverRequest){
        return this.jwtResolver.validateAndExtractSub(serverRequest.headers().firstHeader("Authorization").substring(7));
    }
    private Mono<ServerResponse> buildResponse(Object userSignupRqDto) {
        return ServerResponse.ok().bodyValue(userSignupRqDto);
    }
}
