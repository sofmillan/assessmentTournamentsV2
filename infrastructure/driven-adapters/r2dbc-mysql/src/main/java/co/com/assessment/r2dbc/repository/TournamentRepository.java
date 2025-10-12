package co.com.assessment.r2dbc.repository;

import co.com.assessment.r2dbc.entity.TournamentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TournamentRepository extends ReactiveCrudRepository<TournamentEntity, Integer> {
    Flux<TournamentEntity> findByUserId(String userId);

}
