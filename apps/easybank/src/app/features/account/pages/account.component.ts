import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Account Component
 * User account management and profile
 */
@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>My Account</h1>
      <p>Account management coming soon...</p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
    }
  `],
})
export class AccountComponent {}
