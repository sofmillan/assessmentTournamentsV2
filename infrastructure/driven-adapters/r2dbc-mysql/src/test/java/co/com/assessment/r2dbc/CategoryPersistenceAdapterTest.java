package co.com.assessment.r2dbc;

import co.com.assessment.model.Category;
import co.com.assessment.r2dbc.entity.CategoryEntity;
import co.com.assessment.r2dbc.repository.CategoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
@ExtendWith(MockitoExtension.class)
class CategoryPersistenceAdapterTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ObjectMapper objectMapper;

    private CategoryPersistenceAdapter categoryPersistenceAdapter;

    @BeforeEach
    void setUp(){
        categoryPersistenceAdapter = new CategoryPersistenceAdapter(categoryRepository, objectMapper);
    }

    @Test
    void shouldFindCategoryById(){
        Category category = Category.builder()
                .id(1)
                .build();
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .id(1)
                .build();
        when(categoryRepository.findById(1)).thenReturn(Mono.just(categoryEntity));
        when(objectMapper.map(categoryEntity, Category.class)).thenReturn(category);

        categoryPersistenceAdapter.getCategoryById(1)
                .as(StepVerifier::create)
                .assertNext(Assertions::assertNotNull)
                .verifyComplete();

        verify(categoryRepository).findById(any(Integer.class));
    }

}
