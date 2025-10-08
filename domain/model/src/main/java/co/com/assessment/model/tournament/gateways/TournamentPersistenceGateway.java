package co.com.assessment.model.tournament.gateways;

import co.com.assessment.model.tournament.Tournament;
import reactor.core.publisher.Mono;

public interface TournamentPersistenceGateway {
    Mono<Tournament> saveTournament(Tournament tournament);
 }
