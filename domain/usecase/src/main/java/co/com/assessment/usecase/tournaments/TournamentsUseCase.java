package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.tournament.Tournament;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TournamentsUseCase {

    public Mono<Tournament> createTournament(Mono<Tournament> tournamentMono){
        return Mono.just(new Tournament());
    }
}
