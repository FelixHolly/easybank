import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Balance Component
 * View account balance and transaction history
 */
@Component({
  selector: 'app-balance',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>My Balance</h1>
      <p>Balance information coming soon...</p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
    }
  `],
})
export class BalanceComponent {}
