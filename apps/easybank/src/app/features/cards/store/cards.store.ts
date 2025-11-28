import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService, LoggerService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Card } from '../../../shared/models/financial.model';
import { withUiState } from '../../../core';

/**
 * Card with computed metrics
 */
export interface CardWithMetrics extends Card {
  maskedNumber: string;
  utilizationPercent: number;
  isHighUtilization: boolean;
  isLowUtilization: boolean;
}

/**
 * Helper: Calculate card utilization percentage
 */
function calculateUtilization(card: Card): number {
  if (!card.totalLimit || card.totalLimit <= 0) return 0;
  return Math.min(100, Math.max(0, (card.amountUsed / card.totalLimit) * 100));
}

/**
 * Helper: Mask card number (show only last 4 digits)
 */
function maskCardNumber(cardNumber: string | number): string {
  const num = String(cardNumber ?? '');
  if (!num || num.length < 4) return '•••• •••• •••• ••••';
  const last4 = num.slice(-4);
  return `•••• •••• •••• ${last4}`;
}

/**
 * Cards Store
 * Manages credit/debit cards using NgRx SignalStore
 *
 * Features:
 * - UI state management (loading, error, success)
 * - Computed metrics (utilization, masked numbers)
 * - Computed totals (credit limit, available amount)
 * - Reactive loading with rxMethod
 *
 * @example
 * ```typescript
 * export class CardsComponent {
 *   private cardsStore = inject(CardsStore);
 *
 *   readonly cards = this.cardsStore.cardsWithMetrics;
 *   readonly totalLimit = this.cardsStore.totalCreditLimit;
 *   readonly loading = this.cardsStore.loading;
 *
 *   ngOnInit() {
 *     this.cardsStore.loadCards();
 *   }
 * }
 * ```
 */
export const CardsStore = signalStore(
  { providedIn: 'root' },

  // UI state management
  withUiState<Card[]>(),

  // Computed values
  withComputed(({ data }) => ({
    /**
     * Cards enriched with computed metrics
     */
    cardsWithMetrics: computed((): CardWithMetrics[] => {
      const cards = data() ?? [];
      return cards.map(card => {
        const utilization = calculateUtilization(card);
        return {
          ...card,
          maskedNumber: maskCardNumber(card.cardNumber),
          utilizationPercent: utilization,
          isHighUtilization: utilization >= 80,
          isLowUtilization: utilization > 0 && utilization < 30,
        };
      });
    }),

    /**
     * Total credit limit across all cards
     */
    totalCreditLimit: computed(() => {
      const cards = data() ?? [];
      return cards.reduce((sum, card) => sum + (card.totalLimit || 0), 0);
    }),

    /**
     * Total available credit across all cards
     */
    totalAvailable: computed(() => {
      const cards = data() ?? [];
      return cards.reduce((sum, card) => sum + (card.availableAmount || 0), 0);
    }),

    /**
     * Total amount used across all cards
     */
    totalUsed: computed(() => {
      const cards = data() ?? [];
      return cards.reduce((sum, card) => sum + (card.amountUsed || 0), 0);
    }),

    /**
     * Overall utilization percentage across all cards
     */
    overallUtilization: computed(() => {
      const cards = data() ?? [];
      const totalLimit = cards.reduce((sum, c) => sum + (c.totalLimit || 0), 0);
      const totalUsed = cards.reduce((sum, c) => sum + (c.amountUsed || 0), 0);

      if (totalLimit <= 0) return 0;
      return Math.min(100, Math.max(0, (totalUsed / totalLimit) * 100));
    }),

    /**
     * Cards with high utilization (>= 80%)
     */
    highUtilizationCards: computed(() => {
      const cards = data() ?? [];
      return cards.filter(card => calculateUtilization(card) >= 80);
    }),

    /**
     * Number of active cards
     */
    cardCount: computed(() => (data() ?? []).length),

    /**
     * Check if user has any cards
     */
    hasCards: computed(() => (data() ?? []).length > 0),
  })),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load cards from API
       * Automatically manages loading, error, and success states
       */
      loadCards: rxMethod<void>(
        pipe(
          tap(() => {
            logger.info('Loading cards');
            patchState(store, {
              loading: true,
              error: null,
              success: false,
            });
          }),
          switchMap(() =>
            apiService.get<Card[]>(API_CONFIG.endpoints.cards).pipe(
              tap({
                next: (cards) => {
                  logger.success(`Loaded ${cards.length} cards`);
                  patchState(store, {
                    data: cards,
                    loading: false,
                    success: true,
                    error: null,
                  });
                },
                error: (error) => {
                  const errorMessage = 'Failed to load cards. Please try again.';
                  logger.error('Failed to load cards', error);
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
       * Retry loading cards
       */
      retry() {
        this.loadCards();
      },
    };
  })
);
