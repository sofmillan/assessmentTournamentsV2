package co.com.assessment.model.tournament.gateways;

import co.com.assessment.model.tournament.Category;
import reactor.core.publisher.Mono;

public interface CategoryPersistenceGateway {
    Mono<Category> findCategoryById(Integer id);
}
