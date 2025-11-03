import { inject, Injectable, signal } from '@angular/core';
import { ApiService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { Notice } from '../model/Notice';

@Injectable({
  providedIn: 'root',
})
export class NoticesService {
  private api = inject(ApiService);

  // Signal to hold notices
  readonly notices$ = signal<Notice[]>([]);

  // Optional signal for loading state
  readonly loading$ = signal<boolean>(false);

  // Optional signal for error
  readonly error$ = signal<string | null>(null);

  constructor() {
    this.loadNotices(); // load on creation (you can remove this if you want manual control)
  }

  loadNotices(): void {
    this.loading$.set(true);
    this.error$.set(null);

    this.api.get<Notice[]>(API_CONFIG.endpoints.notices).subscribe({
      next: (notices) => {
        this.notices$.set(notices);
        this.loading$.set(false);
      },
      error: (err) => {
        console.error('Failed to load notices', err);
        this.error$.set('Failed to load notices');
        this.loading$.set(false);
      },
    });
  }
}
