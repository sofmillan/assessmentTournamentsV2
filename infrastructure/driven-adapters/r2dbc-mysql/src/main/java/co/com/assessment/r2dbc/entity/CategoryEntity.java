package co.com.assessment.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("category")
public class CategoryEntity {
    @Id
    private Integer id;
    private String name;
    private Integer capacity;

}
