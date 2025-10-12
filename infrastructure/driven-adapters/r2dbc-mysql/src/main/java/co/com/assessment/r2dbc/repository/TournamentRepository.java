package co.com.assessment.r2dbc.repository;

import co.com.assessment.r2dbc.entity.TournamentEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

// TODO: This file is just an example, you should delete or modify it
public interface TournamentRepository extends ReactiveCrudRepository<TournamentEntity, Integer> {
    Flux<TournamentEntity> findByUserId(String userId);

}
