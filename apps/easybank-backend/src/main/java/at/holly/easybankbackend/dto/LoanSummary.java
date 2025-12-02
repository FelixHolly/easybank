package at.holly.easybankbackend.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Loan Summary DTO
 * Contains aggregate data for all user loans (not just current page)
 * Used in PageResponse to provide global loan statistics
 * alongside paginated loan list
 */
@Getter
@Builder
public class LoanSummary {

  /**
   * Total loan amount borrowed across all loans
   */
  private BigDecimal totalLoanAmount;

  /**
   * Total outstanding amount (remaining to be paid) across all loans
   */
  private BigDecimal totalOutstanding;

  /**
   * Total amount paid across all loans
   */
  private BigDecimal totalPaid;

  /**
   * Number of active loans (outstandingAmount > 0)
   */
  private Long activeLoanCount;

  /**
   * Total number of loans (same as page.totalElements)
   */
  private Long totalLoanCount;
}
