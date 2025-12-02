package at.holly.easybankbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;

/**
 * Generic wrapper for paginated responses with custom metadata
 * Combines Spring Data Page with domain-specific summary/aggregate data
 * <p>
 * Example usage:
 * <pre>{@code
 * PageResponse<AccountTransactionDto, BalanceSummary> response =
 *     PageResponse.of(transactionsPage, balanceSummary);
 * }</pre>
 *
 * @param <T> The content type (e.g., AccountTransactionDto, LoanDto, CardDto)
 * @param <M> The metadata/summary type (e.g., BalanceSummary, LoanSummary)
 */
@Getter
@AllArgsConstructor
public class PageResponse<T, M> {

  /**
   * Spring Data Page containing paginated content and pagination metadata
   */
  private Page<T> page;

  /**
   * Custom domain-specific metadata (aggregates, summaries, totals)
   */
  private M metadata;

  /**
   * Factory method to create PageResponse
   *
   * @param page The Spring Data Page
   * @param metadata Custom metadata/summary
   * @return PageResponse instance
   */
  public static <T, M> PageResponse<T, M> of(Page<T> page, M metadata) {
    return new PageResponse<>(page, metadata);
  }
}
