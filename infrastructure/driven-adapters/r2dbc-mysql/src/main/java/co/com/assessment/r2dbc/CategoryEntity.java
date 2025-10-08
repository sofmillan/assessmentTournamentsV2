package co.com.assessment.r2dbc;

import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class CategoryEntity {
    @Id
    private Long id;
    private String name;
    private Integer capacity;

}
