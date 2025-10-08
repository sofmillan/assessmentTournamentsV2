package co.com.assessment.r2dbc;

import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.gateways.TournamentPersistenceGateway;
import co.com.assessment.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class MyReactivePersistenceGatewayAdapter extends ReactiveAdapterOperations<
        Tournament/* change for domain model */,
        TournamentEntity/* change for adapter model */,
        Integer,
        TournamentRepository
> implements TournamentPersistenceGateway {
    public MyReactivePersistenceGatewayAdapter(TournamentRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Tournament.class/* change for domain model */));
    }


    @Override
    public Mono<Tournament> saveTournament(Tournament tournament) {
        return this.save(tournament);
    }
}
