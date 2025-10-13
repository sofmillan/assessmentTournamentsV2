package co.com.assessment.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("tickets")
public class TicketEntity {

    @Id
    private Long id;
    @Column("user_id")
    private String userId;
    @Column("tournament_id")
    private Integer tournamentId;

    @Column("total_price")
    private Double totalPrice;

    @Column("purchase_date")
    private LocalDateTime purchaseDate;
    private String code;

    @Column("transaction_id")
    private String transactionId;
}
