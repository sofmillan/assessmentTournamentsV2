package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.tournament.PurchaseDetails;
import co.com.assessment.model.tournament.Ticket;
import co.com.assessment.model.tournament.gateways.PaymentGateway;
import co.com.assessment.model.tournament.gateways.TicketPersistenceGateway;
import co.com.assessment.model.tournament.gateways.TournamentPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class TicketsUseCase {
    private final TournamentsUseCase tournamentUseCase;
    private final PaymentGateway paymentGateway;
    private final TicketPersistenceGateway ticketPersistenceGateway;

    public Mono<Ticket> purchaseTicket(PurchaseDetails purchaseDetails, String userId){
        System.out.println("ENTERS USE CASE");
        // 1. Find the Tournament
        return tournamentUseCase.getTournamentById(purchaseDetails.getTournamentId())
                .flatMap(tournament ->{
                    Mono<String> transactionIdMono;
                    if(tournament.isFree()){
                        System.out.println("THIS TOURNAMENT IS FREE");
                        transactionIdMono = Mono.just("");
                    }else{
                        System.out.println("THIS TOURNAMENT IS not free");
                        transactionIdMono = paymentGateway.processPayment(purchaseDetails)
                                .flatMap(confirmation -> {
                                    if (!confirmation.isSuccessful()) {
                                        return Mono.error(new RuntimeException("Payment declined. Details: "));
                                    }
                                    return Mono.just(confirmation.getTransactionDetails().getTransactionId());
                                });

                    }

                    return transactionIdMono
                            .flatMap(transactionId->{
                                Ticket ticket = Ticket.builder()
                                        .code(UUID.randomUUID().toString().substring(0,6))
                                        .tournamentId(tournament.getId())
                                        .userId(userId)
                                        .purchaseDate(LocalDateTime.now())
                                        .build();

                                if (!transactionId.isEmpty()) {
                                    ticket.setTransactionId(transactionId);
                                }

                                tournament.setRemainingCapacity(tournament.getRemainingCapacity()-1);

                                return ticketPersistenceGateway.saveTicket(ticket)
                                        .zipWith(tournamentUseCase.updateRemainingCapacity(tournament));
                            });

                })
                // 5. Extract the Ticket (T1) from the Tuple and return it as Mono<Ticket>
                .map(Tuple2::getT1);
    }
}
