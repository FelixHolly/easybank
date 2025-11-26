package at.holly.easybankbackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cards")
public class Card {

  @Id
  @Column(name = "card_id")
  private long cardId;

  @Column(name = "user_id")
  private long userId;

  @Column(name = "card_number")
  private String cardNumber;

  @Column(name = "card_type")
  private String cardType;

  @Column(name = "total_limit")
  private int totalLimit;

  @Column(name = "amount_used")
  private int amountUsed;

  @Column(name = "available_amount")
  private int availableAmount;

  @Column(name = "create_dt")
  private Date createDt;

}
