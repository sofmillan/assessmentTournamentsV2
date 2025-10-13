package co.com.assessment.r2dbc;

import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.r2dbc.entity.TournamentEntity;
import co.com.assessment.r2dbc.repository.TournamentRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TournamentPersistenceAdapterTest {

    @Mock
    private TournamentRepository tournamentRepository;
    @Mock
    private ObjectMapper objectMapper;

    private TournamentPersistenceAdapter tournamentPersistenceAdapter;

    private Tournament tournament;
    private TournamentEntity tournamentEntity;

    @BeforeEach
    void setUp(){
        tournamentPersistenceAdapter = new TournamentPersistenceAdapter(tournamentRepository, objectMapper);
        tournamentEntity = TournamentEntity.builder()
                .name("Random name")
                .description("Random description")
                .categoryId(1)
                .build();
        tournament = Tournament.builder()
                .name("Random name")
                .description("Random description")
                .categoryId(1)
                .build();
    }

    @Test
    void shouldSaveNewTournament(){
        when(objectMapper.map(tournament, TournamentEntity.class)).thenReturn(tournamentEntity);
        when(tournamentRepository.save(tournamentEntity)).thenReturn(Mono.just(tournamentEntity));
        when(objectMapper.map(tournamentEntity, Tournament.class)).thenReturn(tournament);

        tournamentPersistenceAdapter.saveTournament(tournament)
                .as(StepVerifier::create)
                .assertNext(savedTournament ->{
                    assertNotNull(savedTournament);
                    assertEquals(tournament.getName(), savedTournament.getName());
                }).verifyComplete();

        verify(tournamentRepository).save(any(TournamentEntity.class));
    }

    @Test
    void shouldGetTournamentById(){
        when(tournamentRepository.findById(1)).thenReturn(Mono.just(tournamentEntity));
        when(objectMapper.map(tournamentEntity, Tournament.class)).thenReturn(tournament);

        tournamentPersistenceAdapter.findById(1)
                .as(StepVerifier::create)
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();

        verify(tournamentRepository).findById(any(Integer.class));
    }

    @Test
    void shouldGetAllTournaments(){
        Flux<TournamentEntity> retrievedTournaments = Flux.just(tournamentEntity, tournamentEntity);

        when(tournamentRepository.findAll()).thenReturn(retrievedTournaments);
        when(objectMapper.map(tournamentEntity, Tournament.class)).thenReturn(tournament);

        tournamentPersistenceAdapter.getAllTournaments()
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(tournamentRepository).findAll();
    }

    @Test
    void shouldGetAllTournamentsByUserId(){
        String userId = "1374a27d-1733-4dd8-a1d1-0cb271d1b5cc";
        Flux<TournamentEntity> retrievedTournaments = Flux.just(tournamentEntity);

        when(tournamentRepository.findByUserId(userId)).thenReturn(retrievedTournaments);
        when(objectMapper.map(tournamentEntity, Tournament.class)).thenReturn(tournament);

        tournamentPersistenceAdapter.getTournamentsByUser(userId)
                .as(StepVerifier::create)
                .expectNextCount(1)
                .verifyComplete();

        verify(tournamentRepository).findByUserId(userId);
    }



}
