import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Contact Component
 * Contact form and support information
 */
@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>Contact Us</h1>
      <p>Contact information and form coming soon...</p>
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
    }
  `],
})
export class ContactComponent {}
