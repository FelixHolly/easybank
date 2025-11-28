import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {NoticesStore, NoticeStatus} from '../store/notices.store';
import {Notice} from '../model/Notice';

/**
 * Notices Component
 * View bank notices and announcements
 * Now using NgRx SignalStore for state management
 */
@Component({
  selector: 'app-notices',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notices.component.html',
  styleUrl: './notices.component.scss',
})
export class NoticesComponent implements OnInit {
  private noticesStore = inject(NoticesStore);

  // Expose store signals
  readonly notices = this.noticesStore.noticesWithStatus;
  readonly activeNotices = this.noticesStore.activeNotices;
  readonly upcomingNotices = this.noticesStore.upcomingNotices;
  readonly expiredNotices = this.noticesStore.expiredNotices;
  readonly loading = this.noticesStore.loading;
  readonly error = this.noticesStore.error;
  readonly hasActiveNotices = this.noticesStore.hasActiveNotices;
  readonly hasNotices = this.noticesStore.hasNotices;

  ngOnInit(): void {
    this.noticesStore.loadNotices();
  }

  retry(): void {
    this.noticesStore.retry();
  }

  /**
   * Get status for a notice (data is already computed in store)
   */
  getStatus(notice: Notice): NoticeStatus {
    return this.noticesStore.getStatus(notice);
  }

  /**
   * Get status label
   */
  getStatusLabel(status: NoticeStatus): string {
    return this.noticesStore.getStatusLabel(status);
  }
}
