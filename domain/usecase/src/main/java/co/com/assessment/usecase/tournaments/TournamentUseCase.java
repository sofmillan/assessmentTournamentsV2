package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.Category;
import co.com.assessment.model.Tournament;
import co.com.assessment.model.TournamentMetrics;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.gateways.CategoryPersistenceGateway;
import co.com.assessment.model.gateways.TicketPersistenceGateway;
import co.com.assessment.model.gateways.TournamentPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class TournamentUseCase {
    private final TournamentPersistenceGateway tournamentPersistenceGateway;
    private final CategoryPersistenceGateway categoryPersistenceGateway;
    private final TicketPersistenceGateway ticketPersistenceGateway;
    private static final Long FREE_TOURNAMENT_LIMIT = 2L;
    public Mono<Tournament> createTournament(Tournament tournament){
        Mono<Tournament> validTournamentCreation = tournament.isFree()
                ? checkFreeTournamentLimit(tournament)
                : Mono.just(tournament);

        return validTournamentCreation
                .flatMap(checkedTournament -> categoryPersistenceGateway
                            .getCategoryById(checkedTournament.getCategoryId())
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

    public Mono<TournamentMetrics> getTournamentMetrics(Integer tournamentId){
        return this.getTournamentById(tournamentId)
                .flatMap(tournament -> {
                    Mono<Integer> totalCapacityMono = categoryPersistenceGateway
                            .getCategoryById(tournament.getCategoryId())
                            .map(Category::getCapacity);

                    Mono<Long> soldTicketsMono = ticketPersistenceGateway
                            .getTicketsByTournamentId(tournamentId)
                            .count();

                    return Mono.zip(totalCapacityMono, soldTicketsMono)
                            .map(tuple -> {
                                Integer totalCapacity = tuple.getT1();
                                Integer soldTicketsCount = tuple.getT2().intValue();

                                return TournamentMetrics.builder()
                                        .totalCapacity(totalCapacity)
                                        .numberSoldTickets(soldTicketsCount)
                                        .remainingCapacity(totalCapacity - soldTicketsCount)
                                        .revenue(soldTicketsCount * tournament.getTicketPrice())
                                        .build();
                            });
                });
        }

    private Mono<Tournament> checkFreeTournamentLimit(Tournament tournament) {
        return this.tournamentPersistenceGateway
                .getTournamentsByUser(tournament.getUserId())
                .filter(Tournament::isFree)
                .count()
                .flatMap(count -> {
                    if (count >= FREE_TOURNAMENT_LIMIT) {
                        throw new BusinessException(BusinessErrorMessage.FREE_TOURNAMENTS_EXCEEDED);
                    }
                    return Mono.just(tournament);
                });
    }
}
