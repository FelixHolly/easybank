package at.holly.easybankbackend.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Card Summary DTO
 * Contains aggregate data for all user cards (not just current page)
 * Used in PageResponse to provide global card statistics
 * alongside paginated card list
 */
@Getter
@Builder
public class CardSummary {

  /**
   * Total credit limit across all cards
   */
  private BigDecimal totalCreditLimit;

  /**
   * Total available amount across all cards
   */
  private BigDecimal totalAvailable;

  /**
   * Total amount used across all cards
   */
  private BigDecimal totalUsed;

  /**
   * Overall utilization percentage across all cards (0-100)
   */
  private Double overallUtilization;

  /**
   * Total number of cards (same as page.totalElements)
   */
  private Long cardCount;
}
