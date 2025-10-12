package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.tournament.Category;
import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.exception.BusinessErrorMessage;
import co.com.assessment.model.tournament.exception.BusinessException;
import co.com.assessment.model.tournament.gateways.CategoryPersistenceGateway;
import co.com.assessment.model.tournament.gateways.TournamentPersistenceGateway;
import org.junit.jupiter.api.AssertionsKt;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void shouldCreateTournament(){
        Category category = Category.builder()
                .id(1L)
                .name("Random category")
                .capacity(20)
                .build();
        Tournament tournament = Tournament.builder()
                .name("Random tournament")
                .categoryId(1)
                .build();

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
    void createTournamentShouldThrowBusinessExceptionWhenCategoryNotFound(){
        Tournament tournament = Tournament.builder()
                .name("Random tournament")
                .categoryId(1)
                .build();

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
        Tournament tournament = Tournament.builder()
                .name("Random tournament")
                .categoryId(1)
                .build();
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
}
