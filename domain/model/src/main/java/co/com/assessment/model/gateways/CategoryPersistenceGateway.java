package co.com.assessment.model.gateways;

import co.com.assessment.model.Category;
import reactor.core.publisher.Mono;

public interface CategoryPersistenceGateway {
    Mono<Category> getCategoryById(Integer id);
}
