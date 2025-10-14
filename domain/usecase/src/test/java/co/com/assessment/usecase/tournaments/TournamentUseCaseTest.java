package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.Category;
import co.com.assessment.model.Tournament;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.gateways.CategoryPersistenceGateway;
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
    @InjectMocks
    private TournamentsUseCase tournamentsUseCase;

    private Tournament tournament;
    @BeforeEach
    void setUp(){
        tournament = Tournament.builder()
                .name("Random tournament")
                .categoryId(1)
                .userId("05639cbe-8368-4c64-9a50-28a4f0795f6f")
                .free(true)
                .build();
    }

    @Test
    void shouldCreateFreeTournamentWhenUserHasNotReachLimit(){
        Category category = Category.builder()
                .id(1)
                .name("Random category")
                .capacity(20)
                .build();

        when(categoryPersistenceGateway.findCategoryById(any(Integer.class))).thenReturn(Mono.just(category));
        when(tournamentPersistenceGateway.getTournamentsByUser(any(String.class))).thenReturn(Flux.empty());
        when(tournamentPersistenceGateway.saveTournament(any(Tournament.class))).thenReturn(Mono.just(tournament));

        tournamentsUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .assertNext(createdTournament ->{
                    assertNotNull(createdTournament);
                    assertNotNull(createdTournament.getRemainingCapacity());
                }).verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentsByUser(any(String.class));
        verify(categoryPersistenceGateway).findCategoryById(any(Integer.class));
        verify(tournamentPersistenceGateway).saveTournament(any(Tournament.class));
    }

    @Test
    void shouldCreatePaidTournament(){
        Category category = Category.builder()
                .id(1)
                .name("Random category")
                .capacity(20)
                .build();

        tournament.setFree(false);

        when(categoryPersistenceGateway.findCategoryById(any(Integer.class))).thenReturn(Mono.just(category));
        when(tournamentPersistenceGateway.saveTournament(any(Tournament.class))).thenReturn(Mono.just(tournament));

        tournamentsUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .assertNext(createdTournament ->{
                    assertNotNull(createdTournament);
                    assertNotNull(createdTournament.getRemainingCapacity());
                }).verifyComplete();

        verify(categoryPersistenceGateway).findCategoryById(any(Integer.class));
        verify(tournamentPersistenceGateway).saveTournament(any(Tournament.class));
    }

    @Test
    void createFreeTournamentShouldThrowBusinessExceptionWhenWhenUserHasReachLimit(){
        when(tournamentPersistenceGateway.getTournamentsByUser(any(String.class))).thenReturn(Flux.just(tournament, tournament));

        tournamentsUseCase.createTournament(tournament)
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
        when(categoryPersistenceGateway.findCategoryById(any(Integer.class))).thenReturn(Mono.empty());

        tournamentsUseCase.createTournament(tournament)
                .as(StepVerifier::create)
                .verifyErrorSatisfies(exception ->{
                    assertTrue(exception instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.CATEGORY_NOT_EXIST.getMessage(),
                            exception.getMessage());
                });

        verify(categoryPersistenceGateway).findCategoryById(any(Integer.class));
        verify(tournamentPersistenceGateway, never()).saveTournament(any(Tournament.class));
    }

    @Test
    void shouldGetTournamentById(){
        when(tournamentPersistenceGateway.getTournamentById(any(Integer.class))).thenReturn(Mono.just(tournament));

        tournamentsUseCase.getTournamentById(1)
                .as(StepVerifier::create)
                .assertNext(AssertionsKt::assertNotNull)
                .verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentById(any(Integer.class));
    }

    @Test
    void getTournamentByIdShouldThrowBusinessExceptionWhenTournamentNotFound(){
        when(tournamentPersistenceGateway.getTournamentById(any(Integer.class))).thenReturn(Mono.empty());

        tournamentsUseCase.getTournamentById(1)
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

        tournamentsUseCase.getAllTournaments()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(tournamentPersistenceGateway).getAllTournaments();
    }

    @Test
    void shouldGetAllTournamentsCreatedByUserId(){
        String userId = "edf94586-1259-4e34-b38d-6049e9b87ad0";
        when(tournamentPersistenceGateway.getTournamentsByUser(userId)).thenReturn(Flux.just(new Tournament(), new Tournament()));

        tournamentsUseCase.getTournamentsByUser(userId)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(tournamentPersistenceGateway).getTournamentsByUser(userId);
    }

    @Test
    void shouldUpdateTournamentRemainingCapacity(){
        tournament.setRemainingCapacity(20);
        when(tournamentPersistenceGateway.saveTournament(any(Tournament.class))).thenReturn(Mono.just(tournament));

        tournamentsUseCase.updateRemainingCapacity(tournament)
                .as(StepVerifier::create)
                .assertNext(updatedTournament ->{
                    assertNotNull(updatedTournament);
                    assertNotNull(updatedTournament.getRemainingCapacity());
                    assertEquals(19, updatedTournament.getRemainingCapacity());
                }).verifyComplete();

        verify(tournamentPersistenceGateway).saveTournament(any(Tournament.class));
    }
}
