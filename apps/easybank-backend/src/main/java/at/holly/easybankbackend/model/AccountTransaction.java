package at.holly.easybankbackend.model;

import at.holly.easybankbackend.enums.TransactionType;
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
    name="account_transactions",
    indexes = {
        @Index(name = "idx_transaction_user_id", columnList = "user_id"),
        @Index(name = "idx_transaction_account_number", columnList = "account_number")
    }
)
public class AccountTransaction {

  @Id
  @Column(name = "transaction_id")
  private String transactionId;

  @Column(name="account_number")
  private long accountNumber;

  @Column(name = "user_id")
  private long userId;

  @Column(name="transaction_dt")
  private Date transactionDt;

  @Column(name = "transaction_summary")
  private String transactionSummary;

  @Enumerated(EnumType.STRING)
  @Column(name="transaction_type")
  private TransactionType transactionType;

  @Column(name = "transaction_amt", precision = 19, scale = 2)
  private BigDecimal transactionAmt;

  @Column(name = "closing_balance", precision = 19, scale = 2)
  private BigDecimal closingBalance;

  @Column(name = "create_dt")
  private Date createDt;

}
