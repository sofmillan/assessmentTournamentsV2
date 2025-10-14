package co.com.assessment.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Category {
    private Integer id;
    private String name;
    private Integer capacity;
}
