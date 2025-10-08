package co.com.assessment.r2dbc;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("category")
public class CategoryEntity {
    @Id
    private Long id;
    private String name;
    private Integer capacity;

}
