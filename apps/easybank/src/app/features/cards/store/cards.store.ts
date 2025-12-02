import { computed, inject } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { ApiService, LoggerService, withPagination } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Card } from '../../../shared/models/financial.model';
import { CardsPageResponse, CardSummary } from '../../../shared/models/page-response.model';
import { withUiState } from '../../../core';

/**
 * Card with computed metrics (for individual card display)
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
 * Manages credit/debit cards using NgRx SignalStore with PageResponse
 *
 * Now fetches summary metadata from backend (accurate values from ALL cards)
 *
 * @example
 * ```typescript
 * export class CardsComponent {
 *   readonly cardsStore = inject(CardsStore);
 *
 *   readonly cards = this.cardsStore.cardsWithMetrics;
 *   readonly totalCreditLimit = this.cardsStore.totalCreditLimit;
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

  // Pagination management
  withPagination(),

  // Summary metadata state (from backend)
  withState<CardSummary>({
    totalCreditLimit: 0,
    totalAvailable: 0,
    totalUsed: 0,
    overallUtilization: 0,
    cardCount: 0,
  }),

  // Computed values (only for per-card metrics, not aggregates)
  withComputed(({ data }) => ({
    /**
     * Cards enriched with computed metrics (for individual display only)
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
  })),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load cards from API with pagination and summary metadata
       * Automatically manages loading, error, success, pagination, and summary states
       *
       * @param page Optional page number (defaults to current page)
       */
      loadCards: rxMethod<number | void>(
        pipe(
          tap((page) => {
            const pageNum = page ?? store.currentPage();
            logger.info(`Loading cards (page ${pageNum})`);
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
              'cardId,asc'
            );

            // Build HttpParams from pagination parameters
            let httpParams = new HttpParams()
              .set('page', paginationParams.page.toString())
              .set('size', paginationParams.size.toString());

            if (paginationParams.sort) {
              httpParams = httpParams.set('sort', paginationParams.sort);
            }

            return apiService.get<CardsPageResponse>(
              API_CONFIG.endpoints.cards,
              httpParams
            ).pipe(
              tap({
                next: (response) => {
                  logger.success(
                    `Loaded ${response.page.content.length} cards (page ${response.page.number + 1}/${response.page.totalPages})`
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
                    // Summary metadata (accurate values from ALL cards)
                    totalCreditLimit: response.metadata.totalCreditLimit,
                    totalAvailable: response.metadata.totalAvailable,
                    totalUsed: response.metadata.totalUsed,
                    overallUtilization: response.metadata.overallUtilization,
                    cardCount: response.metadata.cardCount,
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
            );
          })
        )
      ),

      /**
       * Go to next page
       */
      nextPage() {
        if (store.hasNextPage()) {
          this.loadCards(store.currentPage() + 1);
        }
      },

      /**
       * Go to previous page
       */
      previousPage() {
        if (store.hasPreviousPage()) {
          this.loadCards(store.currentPage() - 1);
        }
      },

      /**
       * Go to specific page
       */
      goToPage(page: number) {
        if (page >= 0 && page < store.totalPages()) {
          this.loadCards(page);
        }
      },

      /**
       * Change page size and reload
       */
      changePageSize(size: number) {
        patchState(store, { pageSize: size, currentPage: 0 });
        this.loadCards(0);
      },

      /**
       * Retry loading cards
       */
      retry() {
        this.loadCards();
      },
    };
  })
);
