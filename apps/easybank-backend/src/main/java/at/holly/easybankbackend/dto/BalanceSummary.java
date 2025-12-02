package at.holly.easybankbackend.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Balance Summary DTO
 * Contains aggregate data for all user transactions (not just current page)
 * Used in PageResponse to provide global transaction statistics
 * alongside paginated transaction list
 */
@Getter
@Builder
public class BalanceSummary {

  /**
   * Current account balance from most recent transaction
   */
  private BigDecimal currentBalance;

  /**
   * Total credits (money received) across all transactions
   */
  private BigDecimal totalCredits;

  /**
   * Total debits (money spent) across all transactions
   */
  private BigDecimal totalDebits;

  /**
   * Total number of transactions (same as page.totalElements)
   */
  private Long transactionCount;
}
