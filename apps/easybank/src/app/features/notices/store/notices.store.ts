import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap, map } from 'rxjs';
import { ApiService, LoggerService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Notice } from '../model/Notice';
import { Page, extractPageContent } from '../../../shared/models/page.model';
import { withUiState } from '../../../core';

/**
 * Notice status based on dates
 */
export type NoticeStatus = 'active' | 'upcoming' | 'expired';

/**
 * Notice with computed status
 */
export interface NoticeWithStatus extends Notice {
  status: NoticeStatus;
  statusLabel: string;
}

/**
 * Helper: Determine notice status based on dates
 */
function getNoticeStatus(notice: Notice): NoticeStatus {
  const now = new Date();
  const start = new Date(notice.noticBegDt);
  const end = new Date(notice.noticEndDt);

  if (now < start) return 'upcoming';
  if (now > end) return 'expired';
  return 'active';
}

/**
 * Helper: Get status label
 */
function getStatusLabel(status: NoticeStatus): string {
  switch (status) {
    case 'active':
      return 'Active notice';
    case 'upcoming':
      return 'Scheduled';
    case 'expired':
      return 'Past notice';
  }
}

/**
 * Helper: Check if notice is active
 */
function isNoticeActive(notice: Notice): boolean {
  return getNoticeStatus(notice) === 'active';
}

/**
 * Helper: Check if notice is upcoming
 */
function isNoticeUpcoming(notice: Notice): boolean {
  return getNoticeStatus(notice) === 'upcoming';
}

/**
 * Helper: Check if notice is expired
 */
function isNoticeExpired(notice: Notice): boolean {
  return getNoticeStatus(notice) === 'expired';
}

/**
 * Notices Store
 * Manages bank notices and announcements using NgRx SignalStore
 *
 * Features:
 * - UI state management (loading, error, success)
 * - Computed status (active, upcoming, expired)
 * - Filtered lists by status
 * - Reactive loading with rxMethod
 *
 * @example
 * ```typescript
 * export class NoticesComponent {
 *   private noticesStore = inject(NoticesStore);
 *
 *   readonly notices = this.noticesStore.noticesWithStatus;
 *   readonly activeNotices = this.noticesStore.activeNotices;
 *   readonly loading = this.noticesStore.loading;
 *
 *   ngOnInit() {
 *     this.noticesStore.loadNotices();
 *   }
 * }
 * ```
 */
export const NoticesStore = signalStore(
  { providedIn: 'root' },

  // UI state management
  withUiState<Notice[]>(),

  // Computed values
  withComputed(({ data }) => ({
    /**
     * Notices enriched with computed status
     */
    noticesWithStatus: computed((): NoticeWithStatus[] => {
      const notices = data() ?? [];
      return notices.map(notice => {
        const status = getNoticeStatus(notice);
        return {
          ...notice,
          status,
          statusLabel: getStatusLabel(status),
        };
      });
    }),

    /**
     * Active notices (within date range)
     */
    activeNotices: computed(() => {
      const notices = data() ?? [];
      return notices.filter(notice => isNoticeActive(notice));
    }),

    /**
     * Upcoming notices (start date in future)
     */
    upcomingNotices: computed(() => {
      const notices = data() ?? [];
      return notices.filter(notice => isNoticeUpcoming(notice));
    }),

    /**
     * Expired notices (end date in past)
     */
    expiredNotices: computed(() => {
      const notices = data() ?? [];
      return notices.filter(notice => isNoticeExpired(notice));
    }),

    /**
     * Notices sorted by start date (newest first)
     */
    sortedNotices: computed(() => {
      const notices = [...(data() ?? [])];
      return notices.sort((a, b) =>
        new Date(b.noticBegDt).getTime() - new Date(a.noticBegDt).getTime()
      );
    }),

    /**
     * Count of active notices
     */
    activeNoticeCount: computed(() => {
      const notices = data() ?? [];
      return notices.filter(notice => isNoticeActive(notice)).length;
    }),

    /**
     * Total notice count
     */
    noticeCount: computed(() => (data() ?? []).length),

    /**
     * Check if there are any active notices
     */
    hasActiveNotices: computed(() => {
      const notices = data() ?? [];
      return notices.some(notice => isNoticeActive(notice));
    }),

    /**
     * Check if there are any notices
     */
    hasNotices: computed(() => (data() ?? []).length > 0),
  })),

  // Methods
  withMethods((store) => {
    const apiService = inject(ApiService);
    const logger = inject(LoggerService);

    return {
      /**
       * Load notices from API
       * Automatically manages loading, error, and success states
       */
      loadNotices: rxMethod<void>(
        pipe(
          tap(() => {
            logger.info('Loading notices');
            patchState(store, {
              loading: true,
              error: null,
              success: false,
            });
          }),
          switchMap(() =>
            apiService.get<Page<Notice>>(API_CONFIG.endpoints.notices).pipe(
              map(extractPageContent),
              tap({
                next: (notices) => {
                  logger.success(`Loaded ${notices.length} notices`);
                  patchState(store, {
                    data: notices,
                    loading: false,
                    success: true,
                    error: null,
                  });
                },
                error: (error) => {
                  const errorMessage = 'Failed to load notices. Please try again.';
                  logger.error('Failed to load notices', error);
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
       * Retry loading notices
       */
      retry() {
        this.loadNotices();
      },

      /**
       * Get status for a specific notice
       */
      getStatus(notice: Notice): NoticeStatus {
        return getNoticeStatus(notice);
      },

      /**
       * Get status label for a specific status
       */
      getStatusLabel(status: NoticeStatus): string {
        return getStatusLabel(status);
      },
    };
  })
);
