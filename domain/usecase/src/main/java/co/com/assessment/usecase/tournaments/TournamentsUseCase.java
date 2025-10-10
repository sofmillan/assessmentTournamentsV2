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

        return categoryPersistenceGateway
                .findCategoryById(tournament.getCategoryId())
                .switchIfEmpty(Mono.error(()-> new BusinessException(BusinessErrorMessage.CATEGORY_NOT_EXIST)))
                .flatMap(category -> {
                    tournament.setRemainingCapacity(category.getCapacity());
                    return tournamentPersistenceGateway.saveTournament(tournament);
                });
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


}
