package co.com.assessment.r2dbc.repository;

import co.com.assessment.r2dbc.entity.CategoryEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<CategoryEntity, Integer>{

}
