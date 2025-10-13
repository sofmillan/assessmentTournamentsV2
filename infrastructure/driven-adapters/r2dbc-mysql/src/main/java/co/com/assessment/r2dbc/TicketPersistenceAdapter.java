package co.com.assessment.r2dbc;

import co.com.assessment.model.tournament.Category;
import co.com.assessment.model.tournament.Ticket;
import co.com.assessment.model.tournament.gateways.CategoryPersistenceGateway;
import co.com.assessment.model.tournament.gateways.TicketPersistenceGateway;
import co.com.assessment.r2dbc.entity.CategoryEntity;
import co.com.assessment.r2dbc.entity.TicketEntity;
import co.com.assessment.r2dbc.helper.ReactiveAdapterOperations;
import co.com.assessment.r2dbc.repository.CategoryRepository;
import co.com.assessment.r2dbc.repository.TicketRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Repository
public class TicketPersistenceAdapter extends ReactiveAdapterOperations<
        Ticket,
        TicketEntity,
        Integer,
        TicketRepository
        > implements TicketPersistenceGateway {
    protected TicketPersistenceAdapter(TicketRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Ticket.class));
    }

    @Override
    public Mono<Ticket> saveTicket(Ticket ticket) {
        System.out.println("SAVES TICKET");
        return this.save(ticket);
    }
}
