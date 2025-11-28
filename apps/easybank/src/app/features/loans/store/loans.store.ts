import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap, map } from 'rxjs';
import { ApiService, LoggerService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Loan } from '../../../shared/models/financial.model';
import { Page, extractPageContent } from '../../../shared/models/page.model';
import { withUiState } from '../../../core';

/**
 * Loan stage based on repayment progress
 */
export type LoanStage = 'early' | 'mid' | 'late' | 'completed';

/**
 * Loan with computed metrics
 */
export interface LoanWithMetrics extends Loan {
  repaymentPercent: number;
  stage: LoanStage;
  isFullyRepaid: boolean;
}

/**
 * Helper: Calculate loan repayment percentage
 */
function calculateRepaymentPercent(loan: Loan): number {
  if (!loan.totalLoan || loan.totalLoan <= 0) return 0;
  return Math.min(100, Math.max(0, (loan.amountPaid / loan.totalLoan) * 100));
}

/**
 * Helper: Determine loan stage based on repayment progress
 */
function getLoanStage(loan: Loan): LoanStage {
  const isRepaid = (loan.outstandingAmount ?? 0) <= 0;
  if (isRepaid) return 'completed';

  const percent = calculateRepaymentPercent(loan);
  if (percent < 25) return 'early';
  if (percent < 75) return 'mid';
  return 'late';
}

/**
 * Loans Store
 * Manages loan information using NgRx SignalStore
 *
 * Features:
 * - UI state management (loading, error, success)
 * - Computed metrics (repayment %, stage)
 * - Computed totals (total loans, outstanding amount)
 * - Reactive loading with rxMethod
 *
 * @example
 * ```typescript
 * export class LoansComponent {
 *   private loansStore = inject(LoansStore);
 *
 *   readonly loans = this.loansStore.loansWithMetrics;
 *   readonly totalOutstanding = this.loansStore.totalOutstanding;
 *   readonly loading = this.loansStore.loading;
 *
 *   ngOnInit() {
 *     this.loansStore.loadLoans();
 *   }
 * }
 * ```
 */
export const LoansStore = signalStore(
  { providedIn: 'root' },

  // UI state management
  withUiState<Loan[]>(),

  // Computed values
  withComputed(({ data }) => ({
    /**
     * Loans enriched with computed metrics
     */
    loansWithMetrics: computed((): LoanWithMetrics[] => {
      const loans = data() ?? [];
      return loans.map(loan => ({
        ...loan,
        repaymentPercent: calculateRepaymentPercent(loan),
        stage: getLoanStage(loan),
        isFullyRepaid: (loan.outstandingAmount ?? 0) <= 0,
      }));
    }),

    /**
     * Total loan amount (all loans combined)
     */
    totalLoanAmount: computed(() => {
      const loans = data() ?? [];
      return loans.reduce((sum, loan) => sum + (loan.totalLoan || 0), 0);
    }),

    /**
     * Total outstanding amount (remaining to be paid)
     */
    totalOutstanding: computed(() => {
      const loans = data() ?? [];
      return loans.reduce((sum, loan) => sum + (loan.outstandingAmount || 0), 0);
    }),

    /**
     * Total amount paid across all loans
     */
    totalPaid: computed(() => {
      const loans = data() ?? [];
      return loans.reduce((sum, loan) => sum + (loan.amountPaid || 0), 0);
    }),

    /**
     * Overall repayment percentage across all loans
     */
    overallRepaymentPercent: computed(() => {
      const loans = data() ?? [];
      const totalLoan = loans.reduce((sum, l) => sum + (l.totalLoan || 0), 0);
      const totalPaid = loans.reduce((sum, l) => sum + (l.amountPaid || 0), 0);

      if (totalLoan <= 0) return 0;
      return Math.min(100, Math.max(0, (totalPaid / totalLoan) * 100));
    }),

    /**
     * Active loans (not fully repaid)
     */
    activeLoans: computed(() => {
      const loans = data() ?? [];
      return loans.filter(loan => (loan.outstandingAmount ?? 0) > 0);
    }),

    /**
     * Completed loans (fully repaid)
     */
    completedLoans: computed(() => {
      const loans = data() ?? [];
      return loans.filter(loan => (loan.outstandingAmount ?? 0) <= 0);
    }),

    /**
     * Loans in early stage (< 25% repaid)
     */
    earlyStageLoans: computed(() => {
      const loans = data() ?? [];
      return loans.filter(loan => {
        const percent = calculateRepaymentPercent(loan);
        return percent < 25 && (loan.outstandingAmount ?? 0) > 0;
      });
    }),

    /**
     * Loans in mid stage (25-75% repaid)
     */
    midStageLoans: computed(() => {
      const loans = data() ?? [];
      return loans.filter(loan => {
        const percent = calculateRepaymentPercent(loan);
        return percent >= 25 && percent < 75 && (loan.outstandingAmount ?? 0) > 0;
      });
    }),

    /**
     * Loans in late stage (>= 75% repaid)
     */
    lateStageLoans: computed(() => {
      const loans = data() ?? [];
      return loans.filter(loan => {
        const percent = calculateRepaymentPercent(loan);
        return percent >= 75 && (loan.outstandingAmount ?? 0) > 0;
      });
    }),

    /**
     * Number of loans
     */
    loanCount: computed(() => (data() ?? []).length),

    /**
     * Number of active loans
     */
    activeLoanCount: computed(() => {
      const loans = data() ?? [];
      return loans.filter(l => (l.outstandingAmount ?? 0) > 0).length;
    }),

    /**
     * Check if user has any loans
     */
    hasLoans: computed(() => (data() ?? []).length > 0),
  })),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load loans from API
       * Automatically manages loading, error, and success states
       */
      loadLoans: rxMethod<void>(
        pipe(
          tap(() => {
            logger.info('Loading loans');
            patchState(store, {
              loading: true,
              error: null,
              success: false,
            });
          }),
          switchMap(() =>
            apiService.get<Page<Loan>>(API_CONFIG.endpoints.loans).pipe(
              map(extractPageContent),
              tap({
                next: (loans) => {
                  logger.success(`Loaded ${loans.length} loans`);
                  patchState(store, {
                    data: loans,
                    loading: false,
                    success: true,
                    error: null,
                  });
                },
                error: (error) => {
                  const errorMessage = 'Failed to load loans. Please try again.';
                  logger.error('Failed to load loans', error);
                  patchState(store, {
                    loading: false,
                    error: errorMessage,
                    success: false,
                  });
                },
              })
            )
          )
        )
      ),

      /**
       * Retry loading loans
       */
      retry() {
        this.loadLoans();
      },
    };
  })
);
