package co.com.assessment.r2dbc.entity;



/*import jakarta.persistence.GeneratedValue;

import jakarta.persistence.GenerationType;*/
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDate;

@Table("tournaments")
@Data
public class TournamentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String description;
    private String name;
    @Column("category_id")
    private Integer categoryId;
    @Column("start_date")
    private LocalDate startDate;
    @Column("end_date")
    private LocalDate endDate;

    @Column("user_id")
    private String userId;

    @Column("ticket_price")
    private Double ticketPrice;

    @Column("remaining_capacity")
    private Integer remainingCapacity;

    @Column("is_free")
    private Boolean free;
}
