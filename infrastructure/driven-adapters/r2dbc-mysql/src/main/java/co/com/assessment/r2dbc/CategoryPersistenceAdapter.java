package co.com.assessment.r2dbc;

import co.com.assessment.model.tournament.Category;
import co.com.assessment.model.tournament.gateways.CategoryPersistenceGateway;
import co.com.assessment.r2dbc.entity.CategoryEntity;
import co.com.assessment.r2dbc.helper.ReactiveAdapterOperations;
import co.com.assessment.r2dbc.repository.CategoryRepository;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class CategoryPersistenceAdapter  extends ReactiveAdapterOperations<
        Category/* change for domain model */,
        CategoryEntity/* change for adapter model */,
        Integer,
        CategoryRepository
        > implements CategoryPersistenceGateway {
    protected CategoryPersistenceAdapter(CategoryRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Category.class/* change for domain model */));
    }

    @Override
    public Mono<Category> findCategoryById(Integer id) {
        return this.findById(id);
    }
}
