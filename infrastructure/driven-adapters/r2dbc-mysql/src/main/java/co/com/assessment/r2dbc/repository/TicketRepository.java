package co.com.assessment.r2dbc.repository;

import co.com.assessment.r2dbc.entity.TicketEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface TicketRepository extends ReactiveCrudRepository<TicketEntity, Integer> {
    Flux<TicketEntity> findByTournamentId(Integer tournamentId);
}
