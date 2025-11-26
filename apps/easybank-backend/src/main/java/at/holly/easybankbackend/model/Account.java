package at.holly.easybankbackend.model;

import at.holly.easybankbackend.enums.AccountType;
import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "accounts",
    indexes = {
        @Index(name = "idx_account_user_id", columnList = "user_id")
    }
)
public class Account {

  @Column(name = "user_id")
  private long userId;

  @Id
  @Column(name="account_number")
  private long accountNumber;

  @Enumerated(EnumType.STRING)
  @Column(name="account_type")
  private AccountType accountType;

  @Column(name = "branch_address")
  private String branchAddress;

  @Column(name = "create_dt")
  private Date createDt;

}
