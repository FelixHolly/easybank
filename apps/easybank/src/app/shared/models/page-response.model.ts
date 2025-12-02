import { Page } from './page.model';

/**
 * Generic Page Response with Metadata
 * Mirrors backend PageResponse<T, M> structure
 *
 * Combines Spring Data Page with domain-specific summary/aggregate data
 *
 * @example
 * ```typescript
 * interface BalanceResponse extends PageResponse<AccountTransaction, BalanceSummary> {}
 * ```
 */
export interface PageResponse<T, M> {
  /**
   * Spring Data Page containing paginated content and pagination metadata
   */
  page: Page<T>;

  /**
   * Custom domain-specific metadata (aggregates, summaries, totals)
   */
  metadata: M;
}

/**
 * Balance Summary
 * Contains aggregate data for all user transactions (not just current page)
 */
export interface BalanceSummary {
  /**
   * Current account balance from most recent transaction
   */
  currentBalance: number;

  /**
   * Total credits (money received) across all transactions
   */
  totalCredits: number;

  /**
   * Total debits (money spent) across all transactions
   */
  totalDebits: number;

  /**
   * Total number of transactions
   */
  transactionCount: number;
}

/**
 * Loan Summary
 * Contains aggregate data for all user loans (not just current page)
 */
export interface LoanSummary {
  /**
   * Total loan amount borrowed across all loans
   */
  totalLoanAmount: number;

  /**
   * Total outstanding amount (remaining to be paid) across all loans
   */
  totalOutstanding: number;

  /**
   * Total amount paid across all loans
   */
  totalPaid: number;

  /**
   * Number of active loans (outstandingAmount > 0)
   */
  activeLoanCount: number;

  /**
   * Total number of loans
   */
  totalLoanCount: number;
}

/**
 * Card Summary
 * Contains aggregate data for all user cards (not just current page)
 */
export interface CardSummary {
  /**
   * Total credit limit across all cards
   */
  totalCreditLimit: number;

  /**
   * Total available amount across all cards
   */
  totalAvailable: number;

  /**
   * Total amount used across all cards
   */
  totalUsed: number;

  /**
   * Overall utilization percentage across all cards (0-100)
   */
  overallUtilization: number;

  /**
   * Total number of cards
   */
  cardCount: number;
}

/**
 * Type aliases for specific page responses
 */
export type BalancePageResponse = PageResponse<AccountTransaction, BalanceSummary>;
export type LoansPageResponse = PageResponse<Loan, LoanSummary>;
export type CardsPageResponse = PageResponse<Card, CardSummary>;

// Import types for aliases (these should already exist)
import { AccountTransaction } from './financial.model';
import { Loan } from './financial.model';
import { Card } from './financial.model';
