package co.com.assessment.r2dbc;

import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.gateways.TournamentPersistenceGateway;
import co.com.assessment.r2dbc.entity.TournamentEntity;
import co.com.assessment.r2dbc.helper.ReactiveAdapterOperations;
import co.com.assessment.r2dbc.repository.TournamentRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class TournamentPersistenceAdapter extends ReactiveAdapterOperations<
        Tournament,
        TournamentEntity,
        Integer,
        TournamentRepository
> implements TournamentPersistenceGateway {
    public TournamentPersistenceAdapter(TournamentRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Tournament.class));
    }


    @Override
    public Mono<Tournament> saveTournament(Tournament tournament) {
        return this.save(tournament);
    }

    @Override
    public Mono<Tournament> getTournamentById(Integer id) {
        return this.findById(id);
    }

    @Override
    public Flux<Tournament> getAllTournaments() {
        return this.findAll();
    }

    @Override
    public Flux<Tournament> getTournamentsByUser(String userId) {
        return repository.findByUserId(userId).map(this::toEntity);
    }
}
