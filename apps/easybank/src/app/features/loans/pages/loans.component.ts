import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Loans Component
 * Manage and view loan information
 */
@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>My Loans</h1>
      <p>Loan management coming soon...</p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
    }
  `],
})
export class LoansComponent {}
