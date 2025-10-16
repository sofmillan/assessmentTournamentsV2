package co.com.assessment.api;

import co.com.assessment.api.dto.request.PurchaseTicketRqDto;
import co.com.assessment.api.dto.request.TournamentRqDto;
import co.com.assessment.api.dto.response.DetailedTournamentRsDto;
import co.com.assessment.api.dto.response.TicketRsDto;
import co.com.assessment.api.dto.response.TournamentMetricsRsDto;
import co.com.assessment.api.dto.response.TournamentRsDto;
import co.com.assessment.api.validation.ObjectValidator;
import co.com.assessment.model.PurchaseDetails;
import co.com.assessment.model.Ticket;
import co.com.assessment.model.Tournament;
import co.com.assessment.model.TournamentMetrics;
import co.com.assessment.tokenresolver.JwtResolver;
import co.com.assessment.usecase.tournaments.TicketUseCase;
import co.com.assessment.usecase.tournaments.TournamentUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class HandlerTest {
    @Mock
    private ObjectValidator objectValidator;
    @Mock
    private TournamentUseCase tournamentUseCase;
    @Mock
    private TicketUseCase ticketUseCase;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private JwtResolver jwtResolver;
    @InjectMocks
    private Handler handler;

    private Tournament tournament;
    private String userId;
    @BeforeEach
    void setUp(){
        tournament = Tournament.builder()
                .id(1)
                .name("Random name")
                .description("Random description")
                .ticketPrice(0.0)
                .categoryId(1)
                .free(true)
                .startDate(LocalDate.of(2026,1,1))
                .endDate(LocalDate.of(2026,1,8))
                .build();
        userId = "f33f9324-ed41-4514-aeb3-6971742bd763";
    }

    @Test
    void listenPOSTCreateTournamentWhenValidRequest(){
        TournamentRqDto rqDto = TournamentRqDto.builder()
                .name("Random name")
                .description("Random description")
                .ticketPrice(0.0)
                .categoryId(1)
                .free(true)
                .startDate(LocalDate.of(2026,1,1))
                .endDate(LocalDate.of(2026,1,8))
                .build();

        TournamentRsDto rsDto = TournamentRsDto.builder()
                .name("Random name")
                .ticketPrice(0.0)
                .startDate(LocalDate.of(2026,1,1))
                .endDate(LocalDate.of(2026,1,8))
                .build();

        when(objectMapper.map(rqDto, Tournament.class)).thenReturn(tournament);
        when(tournamentUseCase.createTournament(tournament)).thenReturn(Mono.just(tournament));
        when(objectMapper.map(tournament, TournamentRsDto.class)).thenReturn(rsDto);
        when(jwtResolver.validateAndExtractSub(any(String.class))).thenReturn(userId);

        ServerRequest request = MockServerRequest.builder()
                .header("Authorization", "Bearer mock-access-token")
                .body(Mono.just(rqDto));

        handler.listenPOSTCreateTournament(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(objectValidator).validate(rqDto);
        verify(tournamentUseCase).createTournament(tournament);
    }

    @Test
    void listenGETFindTournamentById(){
        DetailedTournamentRsDto rsDto = DetailedTournamentRsDto.builder()
                .name("Random name")
                .description("Random description")
                .ticketPrice(0.0)
                .startDate(LocalDate.of(2026,1,1))
                .endDate(LocalDate.of(2026,1,8))
                .build();

        when(tournamentUseCase.getTournamentById(1)).thenReturn(Mono.just(tournament));
        when(objectMapper.map(tournament, DetailedTournamentRsDto.class)).thenReturn(rsDto);

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id","1").build();

        handler.listenGETtournamentById(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(tournamentUseCase).getTournamentById(any(Integer.class));
    }

    @Test
    void listenGETAllTournamentsWhenCreatedByMeFalse(){
        Flux<Tournament> retrievedTournaments = Flux.just(tournament, tournament);

        TournamentRsDto rsDto = TournamentRsDto.builder()
                .name("Random name")
                .ticketPrice(0.0)
                .startDate(LocalDate.of(2026,1,1))
                .endDate(LocalDate.of(2026,1,8))
                .build();

        when(tournamentUseCase.getAllTournaments()).thenReturn(retrievedTournaments);
        when(objectMapper.map(tournament, TournamentRsDto.class)).thenReturn(rsDto);

        ServerRequest request = MockServerRequest.builder()
                .queryParam("createdByMe","false").build();

        handler.listenGETAllTournaments(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(tournamentUseCase).getAllTournaments();
    }

    @Test
    void listenGETAllTournamentsWhenCreatedByMeTrue(){
        Flux<Tournament> retrievedTournaments = Flux.just(tournament, tournament);

        TournamentRsDto rsDto = TournamentRsDto.builder()
                .name("Random name")
                .ticketPrice(0.0)
                .startDate(LocalDate.of(2026,1,1))
                .endDate(LocalDate.of(2026,1,8))
                .build();

        when(jwtResolver.validateAndExtractSub(any(String.class))).thenReturn(userId);
        when(tournamentUseCase.getTournamentsByUser(userId)).thenReturn(retrievedTournaments);
        when(objectMapper.map(tournament, TournamentRsDto.class)).thenReturn(rsDto);

        ServerRequest request = MockServerRequest.builder()
                .queryParam("createdByMe", "true")
                .header("Authorization","Bearer mock-access-token").build();

        handler.listenGETAllTournaments(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(tournamentUseCase).getTournamentsByUser(userId);
    }

    @Test
    void listenPOSTPurchaseTicketWhenValidRequest(){
        PurchaseTicketRqDto rqDto = PurchaseTicketRqDto.builder()
                .tournamentId(1)
                .build();
        TicketRsDto rsDto = TicketRsDto.builder()
                .code("2af43a2")
                .totalPrice(0.0)
                .tournamentId(1)
                .build();

        PurchaseDetails purchaseDetails = PurchaseDetails.builder()
                .tournamentId(1)
                .build();
        Ticket ticket = Ticket.builder().build();

        when(jwtResolver.validateAndExtractSub(any(String.class))).thenReturn(userId);
        when(objectMapper.map(rqDto, PurchaseDetails.class)).thenReturn(purchaseDetails);
        when(ticketUseCase.purchaseTicket(purchaseDetails, userId)).thenReturn(Mono.just(ticket));
        when(objectMapper.map(ticket, TicketRsDto.class)).thenReturn(rsDto);

        ServerRequest request = MockServerRequest.builder()
                .header("Authorization", "Bearer mock-access-token")
                .body(Mono.just(rqDto));

        handler.listenPOSTPurchaseTicket(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(objectValidator).validate(rqDto);
        verify(ticketUseCase).purchaseTicket(purchaseDetails,userId);
    }

    @Test
    void listenGETtournamentMetrics(){
        TournamentMetricsRsDto metricsRsDto = TournamentMetricsRsDto.builder()
                .numberSoldTickets(10)
                .remainingCapacity(10)
                .revenue(100.0)
                .totalCapacity(20)
                .build();
        TournamentMetrics metrics = TournamentMetrics.builder()
                .numberSoldTickets(10)
                .remainingCapacity(10)
                .revenue(100.0)
                .totalCapacity(20)
                .build();

        when(tournamentUseCase.getTournamentMetrics(1)).thenReturn(Mono.just(metrics));
        when(objectMapper.map(metrics, TournamentMetricsRsDto.class)).thenReturn(metricsRsDto);

        ServerRequest request = MockServerRequest.builder()
                .pathVariable("id","1").build();

        handler.listenGETtournamentMetrics(request)
                .as(StepVerifier::create)
                .expectNextMatches(serverResponse -> serverResponse.statusCode().is2xxSuccessful())
                .verifyComplete();

        verify(tournamentUseCase).getTournamentMetrics(any(Integer.class));
    }

}
