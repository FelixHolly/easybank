package at.holly.easybankbackend.model;

import at.holly.easybankbackend.enums.LoanType;
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
    name = "loans",
    indexes = {
        @Index(name = "idx_loan_user_id", columnList = "user_id")
    }
)
public class Loan {

  @Id
  @Column(name = "loan_number")
  private long loanNumber;

  @Column(name = "user_id")
  private long userId;

  @Column(name = "start_dt")
  private Date startDt;

  @Enumerated(EnumType.STRING)
  @Column(name = "loan_type")
  private LoanType loanType;

  @Column(name = "total_loan", precision = 19, scale = 2)
  private BigDecimal totalLoan;

  @Column(name = "amount_paid", precision = 19, scale = 2)
  private BigDecimal amountPaid;

  @Column(name = "outstanding_amount", precision = 19, scale = 2)
  private BigDecimal outstandingAmount;

  @Column(name = "create_dt")
  private Date createDt;

}
