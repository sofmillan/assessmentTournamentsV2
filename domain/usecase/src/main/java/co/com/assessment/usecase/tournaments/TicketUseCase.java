package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.PurchaseDetails;
import co.com.assessment.model.Ticket;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.gateways.PaymentGateway;
import co.com.assessment.model.gateways.TicketPersistenceGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class TicketUseCase {
    private final TournamentUseCase tournamentUseCase;
    private final PaymentGateway paymentGateway;
    private final TicketPersistenceGateway ticketPersistenceGateway;
    public static final Double PLATFORM_FEE = 0.05;

    public Mono<Ticket> purchaseTicket(PurchaseDetails purchaseDetails, String userId){
        return tournamentUseCase.getTournamentById(purchaseDetails.getTournamentId())
                .flatMap(tournament ->{
                    if(tournament.getRemainingCapacity()==0) throw new BusinessException(BusinessErrorMessage.TOURNAMENT_SOLD_OUT);

                    Mono<String> transactionIdMono;
                    if(tournament.isFree()){
                        transactionIdMono = Mono.just("");
                    }else{
                        purchaseDetails.setAmount(tournament.getTicketPrice() + (tournament.getTicketPrice() * PLATFORM_FEE));
                        transactionIdMono = paymentGateway.processPayment(purchaseDetails)
                                .flatMap(confirmation -> Mono.just(confirmation.getTransactionDetails().getTransactionId()));
                    }

                    return transactionIdMono
                            .flatMap(transactionId->{
                                Ticket ticket = Ticket.builder()
                                        .code(UUID.randomUUID().toString().substring(0,6))
                                        .tournamentId(tournament.getId())
                                        .userId(userId)
                                        .purchaseDate(LocalDateTime.now())
                                        .transactionId(transactionId)
                                        .totalPrice(tournament.getTicketPrice() == 0 ? tournament.getTicketPrice() : purchaseDetails.getAmount())
                                        .build();

                                return ticketPersistenceGateway.saveTicket(ticket)
                                        .zipWith(tournamentUseCase.updateRemainingCapacity(tournament));
                            });
                })
                .map(Tuple2::getT1);
    }
}
