import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Notices Component
 * System notices and announcements
 */
@Component({
  selector: 'app-notices',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>Notices</h1>
      <p>System notices coming soon...</p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
    }
  `],
})
export class NoticesComponent {}
