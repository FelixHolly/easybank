import { computed } from '@angular/core';
import { signalStoreFeature, withComputed, withMethods, withState } from '@ngrx/signals';

/**
 * Pagination state interface
 */
export interface PaginationState {
  currentPage: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  isFirst: boolean;
  isLast: boolean;
}

/**
 * Default pagination state
 */
const initialPaginationState: PaginationState = {
  currentPage: 0,
  pageSize: 20,
  totalElements: 0,
  totalPages: 0,
  isFirst: true,
  isLast: true,
};

/**
 * Pagination parameters for API requests
 */
export interface PaginationParams {
  page: number;
  size: number;
  sort?: string;
}

/**
 * Composable pagination feature for NgRx SignalStore
 *
 * Provides:
 * - Pagination state (currentPage, pageSize, totalElements, totalPages)
 * - Computed values (hasPreviousPage, hasNextPage, pageNumbers)
 * - Helper methods to build pagination parameters
 *
 * @example
 * ```typescript
 * export const MyStore = signalStore(
 *   { providedIn: 'root' },
 *   withPagination(),
 *   withMethods((store) => ({
 *     loadData: rxMethod<void>(
 *       pipe(
 *         switchMap(() => {
 *           const params = buildPaginationParams(store);
 *           return apiService.get(endpoint, { params });
 *         })
 *       )
 *     )
 *   }))
 * );
 * ```
 */
export function withPagination() {
  return signalStoreFeature(
    withState<PaginationState>(initialPaginationState),

    withComputed(({ currentPage, totalPages, isFirst, isLast, totalElements, pageSize }) => ({
      /**
       * Check if there is a previous page
       */
      hasPreviousPage: computed(() => !isFirst() && currentPage() > 0),

      /**
       * Check if there is a next page
       */
      hasNextPage: computed(() => !isLast() && currentPage() < totalPages() - 1),

      /**
       * Array of page numbers for pagination UI (max 5 pages shown)
       */
      pageNumbers: computed(() => {
        const total = totalPages();
        const current = currentPage();

        if (total <= 5) {
          return Array.from({ length: total }, (_, i) => i);
        }

        // Show current page in the middle with 2 pages on each side
        let start = Math.max(0, current - 2);
        let end = Math.min(total - 1, current + 2);

        // Adjust if at the beginning or end
        if (current < 2) {
          end = Math.min(4, total - 1);
        } else if (current > total - 3) {
          start = Math.max(0, total - 5);
        }

        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
      }),

      /**
       * Current page range (e.g., "1-20 of 100")
       */
      pageRange: computed(() => {
        const total = totalElements();
        if (total === 0) return '0-0 of 0';

        const size = pageSize();
        const page = currentPage();
        const start = page * size + 1;
        const end = Math.min((page + 1) * size, total);

        return `${start}-${end} of ${total}`;
      }),

      /**
       * Check if pagination is needed (more than one page)
       */
      isPaginationNeeded: computed(() => totalPages() > 1),
    })),

    withMethods(() => ({
      /**
       * Build pagination parameters for API request
       */
      buildPaginationParams(page: number, size: number, sort?: string): PaginationParams {
        const params: PaginationParams = { page, size };
        if (sort) params.sort = sort;
        return params;
      },
    }))
  );
}
