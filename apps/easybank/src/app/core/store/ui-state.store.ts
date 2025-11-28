import {patchState, signalStoreFeature, withComputed, withMethods, withState,} from '@ngrx/signals';
import {computed} from '@angular/core';

/**
 * UI State Interface
 * Represents the state of a UI component or feature
 */
export interface UiState<T> {
  data: T | null;
  loading: boolean;
  error: string | null;
  success: boolean;
}

/**
 * Initial UI State
 */
export function initialUiState<T>(): UiState<T> {
  return {
    data: null,
    loading: false,
    error: null,
    success: false,
  };
}

/**
 * NgRx SignalStore Feature for UI State Management
 * This feature can be composed into any SignalStore to add loading/error/success state management
 *
 * @example
 * ```typescript
 * export const AccountStore = signalStore(
 *   { providedIn: 'root' },
 *   withUiState<Account>(),
 *   withMethods((store) => ({
 *     loadAccount: (apiService: ApiService) => {
 *       patchState(store, { loading: true, error: null });
 *       apiService.get<Account>('/account').subscribe({
 *         next: (account) => patchState(store, { data: account, loading: false, success: true }),
 *         error: (err) => patchState(store, { loading: false, error: 'Failed to load account' })
 *       });
 *     }
 *   }))
 * );
 * ```
 */
export function withUiState<T>() {
  return signalStoreFeature(
    withState<UiState<T>>(initialUiState<T>()),
    withComputed(({ data, error, loading, success }) => ({
      // Computed helper: Check if data exists
      hasData: computed(() => data() !== null),

      // Computed helper: Check if there's an error
      hasError: computed(() => error() !== null),

      // Computed helper: Check if idle (not loading, no error, no success)
      isIdle: computed(() => !loading() && !error() && !success()),
    })),
    withMethods((store) => ({
      // Set loading state
      setLoading: () => {
        patchState(store, {
          loading: true,
          error: null,
          success: false,
        });
      },

      // Set success state with data
      setSuccess: (data: T) => {
        patchState(store, {
          data,
          loading: false,
          error: null,
          success: true,
        });
      },

      // Set error state
      setError: (error: string) => {
        patchState(store, {
          loading: false,
          error,
          success: false,
        });
      },

      // Reset to initial state
      reset: () => {
        patchState(store, initialUiState<T>());
      },

      // Update data using an updater function
      updateData: (updater: (current: T | null) => T | null) => {
        patchState(store, (state) => ({
          data: updater(state.data),
        }));
      },
    }))
  );
}

