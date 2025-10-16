package co.com.assessment.r2dbc;

import co.com.assessment.model.Ticket;
import co.com.assessment.r2dbc.entity.TicketEntity;
import co.com.assessment.r2dbc.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class TicketPersistenceAdapterTest {
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private TicketRepository ticketRepository;
    private TicketPersistenceAdapter ticketPersistenceAdapter;

    private Ticket ticket;
    private TicketEntity ticketEntity;
    @BeforeEach
    void setUp(){
        ticketPersistenceAdapter = new TicketPersistenceAdapter(ticketRepository, objectMapper);

        ticket = Ticket.builder()
                .totalPrice(10.0)
                .code("05639c")
                .tournamentId(1)
                .userId("05639cbe-8368-4c64-9a50-28a4f0795f6f")
                .transactionId("15639cae-8368-4c64-9a50-28a4f0795f4d")
                .build();

        ticketEntity = TicketEntity.builder()
                .id(1)
                .totalPrice(10.0)
                .code("05639c")
                .tournamentId(1)
                .userId("05639cbe-8368-4c64-9a50-28a4f0795f6f")
                .transactionId("15639cae-8368-4c64-9a50-28a4f0795f4d")
                .build();
    }

    @Test
    void shouldSaveTicket(){
        when(objectMapper.map(ticket, TicketEntity.class)).thenReturn(ticketEntity);
        when(ticketRepository.save(ticketEntity)).thenReturn(Mono.just(ticketEntity));
        when(objectMapper.map(ticketEntity, Ticket.class)).thenReturn(ticket);

        ticketPersistenceAdapter.saveTicket(ticket)
                .as(StepVerifier::create)
                .assertNext(createdTicket ->{
                    assertNotNull(createdTicket);
                    assertNotNull(createdTicket.getCode());
                    assertNotNull(createdTicket.getTotalPrice());
                }).verifyComplete();

        verify(ticketRepository).save(any(TicketEntity.class));
    }

    @Test
    void shouldGetTicketsByTournamentId(){
        int tournamentId = 1;
        when(ticketRepository.findByTournamentId(tournamentId)).thenReturn(Flux.just(ticketEntity, ticketEntity));
        when(objectMapper.map(ticketEntity, Ticket.class)).thenReturn(ticket);

        ticketPersistenceAdapter.getTicketsByTournamentId(tournamentId)
                .as(StepVerifier::create)
                .expectNextCount(2)
                .verifyComplete();

        verify(ticketRepository).findByTournamentId(tournamentId);
    }


}
