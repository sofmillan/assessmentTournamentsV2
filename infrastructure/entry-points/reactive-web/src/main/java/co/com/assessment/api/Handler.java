package co.com.assessment.api;

import co.com.assessment.api.dto.request.PurchaseTicketRqDto;
import co.com.assessment.api.dto.request.TournamentRqDto;
import co.com.assessment.api.dto.response.*;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.PurchaseDetails;
import co.com.assessment.model.Tournament;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.tokenresolver.JwtResolver;
import co.com.assessment.usecase.tournaments.TicketUseCase;
import co.com.assessment.usecase.tournaments.TournamentUseCase;
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
    private final TournamentUseCase tournamentUseCase;
    private final ObjectMapper objectMapper;
    private final JwtResolver jwtResolver;

    private final TicketUseCase ticketUseCase;


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
                .flatMap(tournamentUseCase::createTournament)
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenGETtournamentById(ServerRequest serverRequest) {
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return tournamentUseCase.getTournamentById(id)
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
                        return tournamentUseCase.getTournamentsByUser(userId);
                    } else {
                        return tournamentUseCase.getAllTournaments();
                    }})
                .map(tournament -> objectMapper.map(tournament, TournamentRsDto.class))
                .collectList()
                .map(tournamentList -> TournamentListRsDto.builder().tournaments(tournamentList).build())
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenPOSTPurchaseTicket(ServerRequest serverRequest) {
        String userId = this.getUser(serverRequest);

        return serverRequest.bodyToMono(PurchaseTicketRqDto.class)
                .switchIfEmpty(Mono.error(() -> new BusinessException(BusinessErrorMessage.INVALID_REQUEST)))
                .doOnNext(objectValidator::validate)
                .map(rqDto -> objectMapper.map(rqDto, PurchaseDetails.class))
                .flatMap(details -> ticketUseCase.purchaseTicket(details, userId))
                .map(ticket -> objectMapper.map(ticket, TicketRsDto.class))
                .flatMap(this::buildResponse);
    }

    public Mono<ServerResponse> listenGETtournamentMetrics(ServerRequest serverRequest){
        Integer id = Integer.valueOf(serverRequest.pathVariable("id"));
        return tournamentUseCase.getTournamentMetrics(id)
                .map(tournamentMetrics -> objectMapper.map(tournamentMetrics, TournamentMetricsRsDto.class))
                .flatMap(this::buildResponse);
    }

    public String getUser(ServerRequest serverRequest){
        return this.jwtResolver.validateAndExtractSub(serverRequest.headers().firstHeader("Authorization").substring(7));
    }
    private Mono<ServerResponse> buildResponse(Object userSignupRqDto) {
        return ServerResponse.ok().bodyValue(userSignupRqDto);
    }
}
