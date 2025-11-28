import { inject } from '@angular/core';
import { patchState, signalStore, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService, LoggerService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Account } from '../../../shared/models/financial.model';
import { withUiState } from '../../../core';

/**
 * Account Store
 * Manages account state using NgRx SignalStore with UI state management
 *
 * Features:
 * - Type-safe state management
 * - Built-in loading/error/success states
 * - Automatic state updates
 * - RxJS integration with rxMethod
 * - Injectable at root level for sharing across components
 *
 * @example
 * ```typescript
 * export class AccountComponent {
 *   private accountStore = inject(AccountStore);
 *
 *   readonly account = this.accountStore.data;
 *   readonly loading = this.accountStore.loading;
 *   readonly error = this.accountStore.error;
 *
 *   ngOnInit() {
 *     this.accountStore.loadAccount();
 *   }
 * }
 * ```
 */
export const AccountStore = signalStore(
  { providedIn: 'root' },

  // Add UI state management (loading, error, success, data)
  withUiState<Account>(),

  // Add custom methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load account data from API
       * Automatically manages loading, error, and success states
       */
      loadAccount: rxMethod<void>(
        pipe(
          tap(() => {
            logger.info('Loading account data');
            patchState(store, {
              loading: true,
              error: null,
              success: false,
            });
          }),
          switchMap(() =>
            apiService.get<Account>(API_CONFIG.endpoints.account).pipe(
              tap({
                next: (account) => {
                  logger.success('Account loaded successfully');
                  patchState(store, {
                    data: account,
                    loading: false,
                    success: true,
                    error: null,
                  });
                },
                error: (error) => {
                  const errorMessage = 'Failed to load account details. Please try again.';
                  logger.error('Failed to load account', error);
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
       * Retry loading account
       */
      retry() {
        this.loadAccount();
      },
    };
  })
);
