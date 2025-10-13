package co.com.assessment.r2dbc.repository;

import co.com.assessment.r2dbc.entity.CategoryEntity;
import co.com.assessment.r2dbc.entity.TicketEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TicketRepository extends ReactiveCrudRepository<TicketEntity, Integer> {
}
