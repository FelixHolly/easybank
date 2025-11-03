import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Cards Component
 * Manage credit/debit cards
 */
@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>My Cards</h1>
      <p>Card management coming soon...</p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
    }
  `],
})
export class CardsComponent {}
