package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.gateways.TournamentPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TournamentsUseCase {
    private final TournamentPersistenceGateway tournamentPersistenceGateway;

    public Mono<Tournament> createTournament(Tournament tournamentMono){
        return tournamentPersistenceGateway.saveTournament(tournamentMono);
    }

    public Mono<Tournament> getTournamentById(Integer id){
        return tournamentPersistenceGateway.getTournamentById(id);
    }
}
