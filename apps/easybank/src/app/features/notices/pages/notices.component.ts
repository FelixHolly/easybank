import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoticesService } from '../services/notices.service';

@Component({
  selector: 'app-notices',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notices.component.html',
  styleUrl: './notices.component.scss',
})
export class NoticesComponent implements OnInit {
  service = inject(NoticesService);

  ngOnInit(): void {
    this.service.loadNotices();
  }

  /**
   * Derive a simple status for the notice based on dates.
   */
  getStatus(notice: any): 'active' | 'upcoming' | 'expired' {
    const now = new Date();
    const start = new Date(notice.noticBegDt);
    const end = new Date(notice.noticEndDt);

    if (now < start) return 'upcoming';
    if (now > end) return 'expired';
    return 'active';
  }

  getStatusLabel(status: 'active' | 'upcoming' | 'expired'): string {
    switch (status) {
      case 'active':
        return 'Active notice';
      case 'upcoming':
        return 'Scheduled';
      case 'expired':
        return 'Past notice';
    }
  }
}
