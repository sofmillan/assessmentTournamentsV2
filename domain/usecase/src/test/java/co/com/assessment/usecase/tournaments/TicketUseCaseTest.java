package co.com.assessment.usecase.tournaments;

import co.com.assessment.model.Confirmation;
import co.com.assessment.model.PurchaseDetails;
import co.com.assessment.model.Ticket;
import co.com.assessment.model.Tournament;
import co.com.assessment.model.exception.BusinessErrorMessage;
import co.com.assessment.model.exception.BusinessException;
import co.com.assessment.model.gateways.PaymentGateway;
import co.com.assessment.model.gateways.TicketPersistenceGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketUseCaseTest {
    @Mock
    private TournamentsUseCase tournamentUseCase;
    @Mock
    private PaymentGateway paymentGateway;
    @Mock
    private TicketPersistenceGateway ticketPersistenceGateway;

    @InjectMocks
    private TicketUseCase ticketUseCase;

    private PurchaseDetails purchaseDetails;
    private String userId;

    @BeforeEach
    void setUp(){
        purchaseDetails = PurchaseDetails.builder()
                .currency("USD")
                .paymentMethod("mock")
                .tournamentId(1)
                .build();
        userId = "1db313d4-c7c3-4a4f-a68c-a388e9a26d70";
    }

    @Test
    void shouldSaveTicketForFreeTournament(){
        Tournament tournament = Tournament.builder()
                .id(1)
                .free(true)
                .ticketPrice(0.0)
                .remainingCapacity(2)
                .build();
        Ticket ticket = Ticket.builder()
                .code(UUID.randomUUID().toString().substring(0,6))
                .tournamentId(tournament.getId())
                .userId(userId)
                .purchaseDate(LocalDateTime.now())
                .totalPrice(0.0)
                .build();

        when(tournamentUseCase.getTournamentById(1)).thenReturn(Mono.just(tournament));
        when(tournamentUseCase.updateRemainingCapacity(tournament)).thenReturn(Mono.just(tournament));
        when(ticketPersistenceGateway.saveTicket(any(Ticket.class))).thenReturn(Mono.just(ticket));

        ticketUseCase.purchaseTicket(purchaseDetails, userId)
                .as(StepVerifier::create)
                .assertNext(savedTicket ->{
                    assertNotNull(savedTicket);
                    assertNotNull(savedTicket.getCode());
                    assertNotNull(savedTicket.getTotalPrice());
                }).verifyComplete();

        verify(tournamentUseCase).updateRemainingCapacity(any(Tournament.class));
        verify(tournamentUseCase).getTournamentById(any(Integer.class));
        verify(ticketPersistenceGateway).saveTicket(any(Ticket.class));
        verify(paymentGateway,never()).processPayment(any(PurchaseDetails.class));
    }

    @Test
    void shouldSaveTicketForPaidTournament(){
        Tournament tournament = Tournament.builder()
                .id(1)
                .free(false)
                .ticketPrice(10.0)
                .remainingCapacity(2)
                .build();

        Ticket ticket = Ticket.builder()
                .code(UUID.randomUUID().toString().substring(0,6))
                .tournamentId(tournament.getId())
                .userId(userId)
                .purchaseDate(LocalDateTime.now())
                .totalPrice(10.5)
                .build();

        Confirmation confirmation = Confirmation.builder()
                .status("SUCCESS")
                .transactionDetails(Confirmation.TransactionDetails.builder()
                        .amountPaid(10.05)
                        .paymentMethod("mock")
                        .transactionId("2ab313d4-c7c3-5a4f-a68c-a388e9a27d71")
                        .build())
                .build();

        when(tournamentUseCase.getTournamentById(1)).thenReturn(Mono.just(tournament));
        when(tournamentUseCase.updateRemainingCapacity(tournament)).thenReturn(Mono.just(tournament));
        when(ticketPersistenceGateway.saveTicket(any(Ticket.class))).thenReturn(Mono.just(ticket));
        when(paymentGateway.processPayment(purchaseDetails)).thenReturn(Mono.just(confirmation));

        ticketUseCase.purchaseTicket(purchaseDetails, userId)
                .as(StepVerifier::create)
                .assertNext(savedTicket ->{
                    assertNotNull(savedTicket);
                    assertNotNull(savedTicket.getCode());
                    assertNotNull(savedTicket.getTotalPrice());
                }).verifyComplete();

        verify(tournamentUseCase).updateRemainingCapacity(any(Tournament.class));
        verify(tournamentUseCase).getTournamentById(any(Integer.class));
        verify(ticketPersistenceGateway).saveTicket(any(Ticket.class));
        verify(paymentGateway).processPayment(any(PurchaseDetails.class));
    }

    @Test
    void purchaseTicketShouldThrowBusinessExceptionWhenTournamentSoldOut(){
        Tournament tournament = Tournament.builder()
                .id(1)
                .free(false)
                .ticketPrice(10.0)
                .remainingCapacity(0)
                .build();

        when(tournamentUseCase.getTournamentById(1)).thenReturn(Mono.just(tournament));

        ticketUseCase.purchaseTicket(purchaseDetails, userId)
                .as(StepVerifier::create)
                .verifyErrorSatisfies(exception ->{
                    assertTrue(exception instanceof BusinessException);
                    assertEquals(BusinessErrorMessage.TOURNAMENT_SOLD_OUT.getMessage(),
                            exception.getMessage());
                });

        verify(tournamentUseCase).getTournamentById(any(Integer.class));
        verify(tournamentUseCase,never()).updateRemainingCapacity(any(Tournament.class));
        verify(ticketPersistenceGateway,never()).saveTicket(any(Ticket.class));
        verify(paymentGateway,never()).processPayment(any(PurchaseDetails.class));
    }
}
