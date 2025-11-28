import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService, LoggerService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { AccountTransaction } from '../../../shared/models/financial.model';
import { withUiState } from '../../../core';

/**
 * Balance Store
 * Manages account balance and transactions using NgRx SignalStore
 *
 * Features:
 * - UI state management (loading, error, success)
 * - Computed values (balance, totals)
 * - Reactive loading with rxMethod
 *
 * @example
 * ```typescript
 * export class BalanceComponent {
 *   private balanceStore = inject(BalanceStore);
 *
 *   readonly transactions = this.balanceStore.sortedTransactions;
 *   readonly currentBalance = this.balanceStore.currentBalance;
 *   readonly loading = this.balanceStore.loading;
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

  // Computed values
  withComputed(({ data }) => ({
    /**
     * Current account balance from most recent transaction
     */
    currentBalance: computed(() => {
      const transactions = data() ?? [];
      if (transactions.length === 0) return 0;

      // Find most recent transaction
      const mostRecent = transactions.reduce((latest: AccountTransaction, tx: AccountTransaction) =>
        new Date(tx.transactionDt) > new Date(latest.transactionDt) ? tx : latest
      , transactions[0]);

      return mostRecent.closingBalance;
    }),

    /**
     * Total credit amount (money received)
     */
    totalCredits: computed(() => {
      const transactions = data() ?? [];
      return transactions
        .filter((tx: AccountTransaction) => tx.transactionType === 'Credit')
        .reduce((sum: number, tx: AccountTransaction) => sum + tx.transactionAmt, 0);
    }),

    /**
     * Total debit amount (money spent)
     */
    totalDebits: computed(() => {
      const transactions = data() ?? [];
      return transactions
        .filter((tx: AccountTransaction) => tx.transactionType === 'Debit')
        .reduce((sum: number, tx: AccountTransaction) => sum + tx.transactionAmt, 0);
    }),

    /**
     * Transactions sorted by date (newest first)
     */
    sortedTransactions: computed(() => {
      const transactions = data() ?? [];
      return [...transactions].sort((a, b) =>
        new Date(b.transactionDt).getTime() - new Date(a.transactionDt).getTime()
      );
    }),

    /**
     * Count of transactions
     */
    transactionCount: computed(() => (data() ?? []).length),

    /**
     * Check if there are any transactions
     */
    hasTransactions: computed(() => (data() ?? []).length > 0),
  })),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load transactions from API
       * Automatically manages loading, error, and success states
       */
      loadTransactions: rxMethod<void>(
        pipe(
          tap(() => {
            logger.info('Loading transactions');
            patchState(store, {
              loading: true,
              error: null,
              success: false,
            });
          }),
          switchMap(() =>
            apiService.get<AccountTransaction[]>(API_CONFIG.endpoints.balance).pipe(
              tap({
                next: (transactions) => {
                  logger.success(`Loaded ${transactions.length} transactions`);
                  patchState(store, {
                    data: transactions,
                    loading: false,
                    success: true,
                    error: null,
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
            )
          )
        )
      ),

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
