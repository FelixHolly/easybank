import { inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService, LoggerService, withPagination } from '../../../core';
import { API_CONFIG } from '../../../config';
import { AccountTransaction } from '../../../shared/models/financial.model';
import { BalancePageResponse, BalanceSummary } from '../../../shared/models/page-response.model';
import { withUiState } from '../../../core';

/**
 * Balance Store
 * Manages account balance and transactions using NgRx SignalStore with PageResponse
 *
 * Now fetches summary metadata from backend (accurate values from ALL transactions)
 *
 * @example
 * ```typescript
 * export class BalanceComponent {
 *   readonly balanceStore = inject(BalanceStore);
 *
 *   readonly transactions = this.balanceStore.data;
 *   readonly currentBalance = this.balanceStore.currentBalance;
 *   readonly totalCredits = this.balanceStore.totalCredits;
 *
 *   ngOnInit() {
 *     this.balanceStore.loadTransactions();
 *   }
 * }
 * ```
 */
export const BalanceStore = signalStore(
  { providedIn: 'root' },

  // UI state management
  withUiState<AccountTransaction[]>(),

  // Pagination management
  withPagination(),

  // Summary metadata state (from backend)
  withState<BalanceSummary>({
    currentBalance: 0,
    totalCredits: 0,
    totalDebits: 0,
    transactionCount: 0,
  }),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load transactions from API with pagination and summary metadata
       * Automatically manages loading, error, success, pagination, and summary states
       *
       * @param page Optional page number (defaults to current page)
       */
      loadTransactions: rxMethod<number | void>(
        pipe(
          tap((page) => {
            const pageNum = page ?? store.currentPage();
            logger.info(`Loading transactions (page ${pageNum})`);
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
              'transactionDt,desc'
            );

            // Build HttpParams from pagination parameters
            let httpParams = new HttpParams()
              .set('page', paginationParams.page.toString())
              .set('size', paginationParams.size.toString());

            if (paginationParams.sort) {
              httpParams = httpParams.set('sort', paginationParams.sort);
            }

            return apiService.get<BalancePageResponse>(
              API_CONFIG.endpoints.balance,
              httpParams
            ).pipe(
              tap({
                next: (response) => {
                  logger.success(
                    `Loaded ${response.page.content.length} transactions (page ${response.page.number + 1}/${response.page.totalPages})`
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
                    // Summary metadata (accurate values from ALL transactions)
                    currentBalance: response.metadata.currentBalance,
                    totalCredits: response.metadata.totalCredits,
                    totalDebits: response.metadata.totalDebits,
                    transactionCount: response.metadata.transactionCount,
                  });
                },
                error: (error) => {
                  const errorMessage = 'Failed to load transactions. Please try again.';
                  logger.error('Failed to load transactions', error);
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
          this.loadTransactions(store.currentPage() + 1);
        }
      },

      /**
       * Go to previous page
       */
      previousPage() {
        if (store.hasPreviousPage()) {
          this.loadTransactions(store.currentPage() - 1);
        }
      },

      /**
       * Go to specific page
       */
      goToPage(page: number) {
        if (page >= 0 && page < store.totalPages()) {
          this.loadTransactions(page);
        }
      },

      /**
       * Change page size and reload
       */
      changePageSize(size: number) {
        patchState(store, { pageSize: size, currentPage: 0 });
        this.loadTransactions(0);
      },

      /**
       * Retry loading transactions
       */
      retry() {
        this.loadTransactions();
      },

      /**
       * Check if transaction is a credit
       */
      isCredit(transaction: AccountTransaction): boolean {
        return transaction.transactionType === 'Credit';
      },

      /**
       * Check if transaction is a debit
       */
      isDebit(transaction: AccountTransaction): boolean {
        return transaction.transactionType === 'Debit';
      },
    };
  })
);
