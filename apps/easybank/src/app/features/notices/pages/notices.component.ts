import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NoticesService } from '../services/notices.service';

@Component({
  selector: 'app-notices',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>System Notices</h1>

      <!-- Loading state -->
      <p *ngIf="service.loading$()">Loading notices...</p>

      <!-- Error state -->
      <p *ngIf="service.error$()">{{ service.error$() }}</p>

      <!-- Notices list -->
      <ul *ngIf="!service.loading$() && !service.error$()">
        <li *ngFor="let notice of service.notices$()">
          <h3>{{ notice.noticeSummary }}</h3>
          <p>{{ notice.noticeDetails }}</p>
          <small>
            From: {{ notice.noticBegDt | date:'mediumDate' }}
            &nbsp;–&nbsp;
            To: {{ notice.noticEndDt | date:'mediumDate' }}
          </small>
        </li>
      </ul>

      <!-- No notices -->
      <p *ngIf="!service.loading$() && service.notices$().length === 0 && !service.error$()">
        No notices available.
      </p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
      max-width: 700px;
      margin: 0 auto;
    }

    ul {
      list-style: none;
      padding: 0;
    }

    li {
      margin-bottom: 1.5rem;
      padding: 1rem;
      border-radius: 8px;
      background: #f5f5f5;
    }

    h3 {
      margin: 0 0 0.5rem;
    }

    small {
      color: #666;
    }
  `],
})
export class NoticesComponent implements OnInit {
  service = inject(NoticesService);

  ngOnInit(): void {
    this.service.loadNotices();
  }
}
