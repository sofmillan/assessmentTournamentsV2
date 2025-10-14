package co.com.assessment.model.gateways;

import co.com.assessment.model.Ticket;
import reactor.core.publisher.Mono;

public interface TicketPersistenceGateway {

    Mono<Ticket> saveTicket(Ticket ticket);
}
