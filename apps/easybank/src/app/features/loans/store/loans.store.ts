import { computed, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService, LoggerService, withPagination } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Loan } from '../../../shared/models/financial.model';
import { LoansPageResponse, LoanSummary } from '../../../shared/models/page-response.model';
import { withUiState } from '../../../core';

/**
 * Loan stage based on repayment progress
 */
export type LoanStage = 'early' | 'mid' | 'late' | 'completed';

/**
 * Loan with computed metrics (for individual loan display)
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
 * Manages loan information using NgRx SignalStore with PageResponse
 *
 * Now fetches summary metadata from backend (accurate values from ALL loans)
 *
 * @example
 * ```typescript
 * export class LoansComponent {
 *   readonly loansStore = inject(LoansStore);
 *
 *   readonly loans = this.loansStore.loansWithMetrics;
 *   readonly totalOutstanding = this.loansStore.totalOutstanding;
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

  // Pagination management
  withPagination(),

  // Summary metadata state (from backend)
  withState<LoanSummary>({
    totalLoanAmount: 0,
    totalOutstanding: 0,
    totalPaid: 0,
    activeLoanCount: 0,
    totalLoanCount: 0,
  }),

  // Computed values (only for per-loan metrics, not aggregates)
  withComputed(({ data }) => ({
    /**
     * Loans enriched with computed metrics (for individual display only)
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
  })),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load loans from API with pagination and summary metadata
       * Automatically manages loading, error, success, pagination, and summary states
       *
       * @param page Optional page number (defaults to current page)
       */
      loadLoans: rxMethod<number | void>(
        pipe(
          tap((page) => {
            const pageNum = page ?? store.currentPage();
            logger.info(`Loading loans (page ${pageNum})`);
            patchState(store, {
              loading: true,
              error: null,
              success: false,
            });
          }),
          switchMap((page) => {
            const pageNum = page ?? store.currentPage();
            const paginationParams = store.buildPaginationParams(
              pageNum,
              store.pageSize(),
              'loanNumber,asc'
            );

            // Build HttpParams from pagination parameters
            let httpParams = new HttpParams()
              .set('page', paginationParams.page.toString())
              .set('size', paginationParams.size.toString());

            if (paginationParams.sort) {
              httpParams = httpParams.set('sort', paginationParams.sort);
            }

            return apiService.get<LoansPageResponse>(
              API_CONFIG.endpoints.loans,
              httpParams
            ).pipe(
              tap({
                next: (response) => {
                  logger.success(
                    `Loaded ${response.page.content.length} loans (page ${response.page.number + 1}/${response.page.totalPages})`
                  );
                  patchState(store, {
                    // Data state
                    data: response.page.content,
                    loading: false,
                    success: true,
                    error: null,
                    // Pagination state
                    currentPage: response.page.number,
                    pageSize: response.page.size,
                    totalElements: response.page.totalElements,
                    totalPages: response.page.totalPages,
                    isFirst: response.page.first,
                    isLast: response.page.last,
                    // Summary metadata (accurate values from ALL loans)
                    totalLoanAmount: response.metadata.totalLoanAmount,
                    totalOutstanding: response.metadata.totalOutstanding,
                    totalPaid: response.metadata.totalPaid,
                    activeLoanCount: response.metadata.activeLoanCount,
                    totalLoanCount: response.metadata.totalLoanCount,
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
            );
          })
        )
      ),

      /**
       * Go to next page
       */
      nextPage() {
        if (store.hasNextPage()) {
          this.loadLoans(store.currentPage() + 1);
        }
      },

      /**
       * Go to previous page
       */
      previousPage() {
        if (store.hasPreviousPage()) {
          this.loadLoans(store.currentPage() - 1);
        }
      },

      /**
       * Go to specific page
       */
      goToPage(page: number) {
        if (page >= 0 && page < store.totalPages()) {
          this.loadLoans(page);
        }
      },

      /**
       * Change page size and reload
       */
      changePageSize(size: number) {
        patchState(store, { pageSize: size, currentPage: 0 });
        this.loadLoans(0);
      },

      /**
       * Retry loading loans
       */
      retry() {
        this.loadLoans();
      },
    };
  })
);
