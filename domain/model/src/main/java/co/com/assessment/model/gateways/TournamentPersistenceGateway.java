package co.com.assessment.model.gateways;

import co.com.assessment.model.Tournament;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TournamentPersistenceGateway {
    Mono<Tournament> saveTournament(Tournament tournament);
    Mono<Tournament> getTournamentById(Integer id);
    Flux<Tournament> getAllTournaments();
    Flux<Tournament> getTournamentsByUser(String userId);
 }
