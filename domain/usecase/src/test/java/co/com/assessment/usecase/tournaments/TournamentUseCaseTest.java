package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.Category;
import co.com.assessment.model.Ticket;
import co.com.assessment.model.Tournament;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.gateways.CategoryPersistenceGateway;
import co.com.assessment.model.gateways.TicketPersistenceGateway;
import co.com.assessment.model.gateways.TournamentPersistenceGateway;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.AssertionsKt.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TournamentUseCaseTest {
    @Mock
    private TournamentPersistenceGateway tournamentPersistenceGateway;
    @Mock
    private CategoryPersistenceGateway categoryPersistenceGateway;
    @Mock
    private TicketPersistenceGateway ticketPersistenceGateway;
    @InjectMocks
    private TournamentUseCase tournamentUseCase;

    private Tournament tournament;
    private Category category;
    @BeforeEach
    void setUp(){
        tournament = Tournament.builder()
                .name("Random tournament")
                .categoryId(1)
                .userId("05639cbe-8368-4c64-9a50-28a4f0795f6f")
                .free(true)
                .remainingCapacity(19)
                .build();

        category = Category.builder()
                .id(1)
                .name("Random category")
                .capacity(20)
                .build();

    }

    @Test
    void shouldCreateFreeTournamentWhenUserHasNotReachLimit(){
        when(categoryPersistenceGateway.getCategoryById(any(Integer.class))).thenReturn(Mono.just(category));
        when(tournamentPersistenceGateway.getTournamentsByUser(any(String.class))).thenReturn(Flux.empty());
        when(tournamentPersistenceGateway.saveTournament(any(Tournament.class))).thenReturn(Mono.just(tournament));

        tournamentUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .assertNext(createdTournament ->{
                    assertNotNull(createdTournament);
                    assertNotNull(createdTournament.getRemainingCapacity());
                }).verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentsByUser(any(String.class));
        verify(categoryPersistenceGateway).getCategoryById(any(Integer.class));
        verify(tournamentPersistenceGateway).saveTournament(any(Tournament.class));
    }

    @Test
    void shouldCreatePaidTournament(){
        tournament.setFree(false);

        when(categoryPersistenceGateway.getCategoryById(any(Integer.class))).thenReturn(Mono.just(category));
        when(tournamentPersistenceGateway.saveTournament(any(Tournament.class))).thenReturn(Mono.just(tournament));

        tournamentUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .assertNext(createdTournament ->{
                    assertNotNull(createdTournament);
                    assertNotNull(createdTournament.getRemainingCapacity());
                }).verifyComplete();

        verify(categoryPersistenceGateway).getCategoryById(any(Integer.class));
        verify(tournamentPersistenceGateway).saveTournament(any(Tournament.class));
    }

    @Test
    void createFreeTournamentShouldThrowBusinessExceptionWhenWhenUserHasReachLimit(){
        when(tournamentPersistenceGateway.getTournamentsByUser(any(String.class))).thenReturn(Flux.just(tournament, tournament));

        tournamentUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .verifyErrorSatisfies(exception ->{
                    assertTrue(exception instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.FREE_TOURNAMENTS_EXCEEDED.getMessage(),
                            exception.getMessage());
                });

        verify(tournamentPersistenceGateway).getTournamentsByUser(any(String.class));
        verify(tournamentPersistenceGateway, never()).saveTournament(any(Tournament.class));
    }


    @Test
    void createTournamentShouldThrowBusinessExceptionWhenCategoryNotFound(){
        when(tournamentPersistenceGateway.getTournamentsByUser(any(String.class))).thenReturn(Flux.empty());
        when(categoryPersistenceGateway.getCategoryById(any(Integer.class))).thenReturn(Mono.empty());

        tournamentUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .verifyErrorSatisfies(exception ->{
                    assertTrue(exception instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.CATEGORY_NOT_EXIST.getMessage(),
                            exception.getMessage());
                });

        verify(categoryPersistenceGateway).getCategoryById(any(Integer.class));
        verify(tournamentPersistenceGateway, never()).saveTournament(any(Tournament.class));
    }

    @Test
    void shouldGetTournamentById(){
        when(tournamentPersistenceGateway.getTournamentById(any(Integer.class))).thenReturn(Mono.just(tournament));

        tournamentUseCase.getTournamentById(1)
                .as(StepVerifier::create)
                .assertNext(AssertionsKt::assertNotNull)
                .verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentById(any(Integer.class));
    }

    @Test
    void getTournamentByIdShouldThrowBusinessExceptionWhenTournamentNotFound(){
        when(tournamentPersistenceGateway.getTournamentById(any(Integer.class))).thenReturn(Mono.empty());

        tournamentUseCase.getTournamentById(1)
                .as(StepVerifier::create)
                .verifyErrorSatisfies(exception ->{
                    assertTrue(exception instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.TOURNAMENT_NOT_EXIST.getMessage(),
                            exception.getMessage());
                });

        verify(tournamentPersistenceGateway).getTournamentById(any(Integer.class));
    }

    @Test
    void shouldGetAllTournaments(){
        when(tournamentPersistenceGateway.getAllTournaments()).thenReturn(Flux.just(new Tournament(), new Tournament()));

        tournamentUseCase.getAllTournaments()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(tournamentPersistenceGateway).getAllTournaments();
    }

    @Test
    void shouldGetAllTournamentsCreatedByUserId(){
        String userId = "edf94586-1259-4e34-b38d-6049e9b87ad0";
        when(tournamentPersistenceGateway.getTournamentsByUser(userId)).thenReturn(Flux.just(new Tournament(), new Tournament()));

        tournamentUseCase.getTournamentsByUser(userId)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentsByUser(userId);
    }

    @Test
    void shouldUpdateTournamentRemainingCapacity(){
        tournament.setRemainingCapacity(20);
        when(tournamentPersistenceGateway.saveTournament(any(Tournament.class))).thenReturn(Mono.just(tournament));

        tournamentUseCase.updateRemainingCapacity(tournament)
                .as(StepVerifier::create)
                .assertNext(updatedTournament ->{
                    assertNotNull(updatedTournament);
                    assertNotNull(updatedTournament.getRemainingCapacity());
                    assertEquals(19, updatedTournament.getRemainingCapacity());
                }).verifyComplete();

        verify(tournamentPersistenceGateway).saveTournament(any(Tournament.class));
    }

    @Test
    void shouldGetTournamentMetrics(){
        int tournamentId = 1;
        tournament.setFree(false);
        tournament.setTicketPrice(100.0);
        Ticket ticket = Ticket.builder()
                .tournamentId(tournamentId)
                .totalPrice(105.0)
                .code("abc123")
                .build();
        when(tournamentPersistenceGateway.getTournamentById(tournamentId)).thenReturn(Mono.just(tournament));
        when(categoryPersistenceGateway.getCategoryById(1)).thenReturn(Mono.just(category));
        when(ticketPersistenceGateway.getTicketsByTournamentId(tournamentId)).thenReturn(Flux.just(ticket));

        tournamentUseCase.getTournamentMetrics(tournamentId)
                .as(StepVerifier::create)
                .assertNext(metrics ->{
                    assertNotNull(metrics);
                    assertNotNull(metrics.getNumberSoldTickets());
                    assertNotNull(metrics.getRemainingCapacity());
                    assertNotNull(metrics.getRevenue());
                    assertNotNull(metrics.getTotalCapacity());
                }).verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentById(any(Integer.class));
        verify(categoryPersistenceGateway).getCategoryById(any(Integer.class));
        verify(ticketPersistenceGateway).getTicketsByTournamentId(any(Integer.class));
    }

}
