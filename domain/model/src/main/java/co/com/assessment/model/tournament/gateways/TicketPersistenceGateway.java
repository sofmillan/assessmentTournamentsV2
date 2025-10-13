package co.com.assessment.model.tournament.gateways;

import co.com.assessment.model.tournament.Ticket;
import reactor.core.publisher.Mono;

public interface TicketPersistenceGateway {

    Mono<Ticket> saveTicket(Ticket ticket);
}
