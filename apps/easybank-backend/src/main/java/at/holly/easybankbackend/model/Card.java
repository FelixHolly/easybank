package at.holly.easybankbackend.model;

import at.holly.easybankbackend.enums.CardType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "cards",
    indexes = {
        @Index(name = "idx_card_user_id", columnList = "user_id")
    }
)
public class Card {

  @Id
  @Column(name = "card_id")
  private long cardId;

  @Column(name = "user_id")
  private long userId;

  @Column(name = "card_number")
  private String cardNumber;

  @Enumerated(EnumType.STRING)
  @Column(name = "card_type")
  private CardType cardType;

  @Column(name = "total_limit", precision = 19, scale = 2)
  private BigDecimal totalLimit;

  @Column(name = "amount_used", precision = 19, scale = 2)
  private BigDecimal amountUsed;

  @Column(name = "available_amount", precision = 19, scale = 2)
  private BigDecimal availableAmount;

  @Column(name = "create_dt")
  private Date createDt;

}
