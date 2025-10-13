package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.tournament.Tournament;
import co.com.assessment.model.tournament.exception.BusinessErrorMessage;
import co.com.assessment.model.tournament.exception.BusinessException;
import co.com.assessment.model.tournament.gateways.CategoryPersistenceGateway;
import co.com.assessment.model.tournament.gateways.TournamentPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TournamentsUseCase {
    private final TournamentPersistenceGateway tournamentPersistenceGateway;
    private final CategoryPersistenceGateway categoryPersistenceGateway;
    public Mono<Tournament> createTournament(Tournament tournament){
        Mono<Tournament> validTournamentCreation = tournament.isFree()
                ? checkFreeTournamentLimit(tournament)
                : Mono.just(tournament);

        return validTournamentCreation
                .flatMap(checkedTournament -> categoryPersistenceGateway
                            .findCategoryById(checkedTournament.getCategoryId())
                            .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.CATEGORY_NOT_EXIST)))
                            .flatMap(category -> {
                                checkedTournament.setRemainingCapacity(category.getCapacity());
                                return tournamentPersistenceGateway.saveTournament(checkedTournament);
                            })
                );
    }

    public Mono<Tournament> getTournamentById(Integer id){
        return tournamentPersistenceGateway
                .getTournamentById(id)
                .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.TOURNAMENT_NOT_EXIST)));
    }

    public Flux<Tournament> getAllTournaments(){
        return tournamentPersistenceGateway.getAllTournaments();
    }
    public Flux<Tournament> getTournamentsByUser(String userId){
        return tournamentPersistenceGateway.getTournamentsByUser(userId);
    }

    public Mono<Tournament> updateRemainingCapacity(Tournament tournament){
        tournament.setRemainingCapacity(tournament.getRemainingCapacity()-1);
        return tournamentPersistenceGateway.saveTournament(tournament);
    }

    private Mono<Tournament> checkFreeTournamentLimit(Tournament tournament) {
        return this.tournamentPersistenceGateway
                .getTournamentsByUser(tournament.getUserId())
                .filter(Tournament::isFree)
                .count()
                .flatMap(count -> {
                    if (count >= 2L) {
                        throw new BusinessException(BusinessErrorMessage.FREE_TOURNAMENTS_EXCEEDED);
                    }
                    return Mono.just(tournament);
                });
    }
}
