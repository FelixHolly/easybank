import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config/api.config';
import { Card } from '../../../shared/models/financial.model';

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

      @if (isLoading()) {
        <p>Loading cards...</p>
      } @else if (errorMessage()) {
        <div class="error-message">{{ errorMessage() }}</div>
      } @else if (cards().length === 0) {
        <p>You have no cards at this time.</p>
      } @else {
        <div class="cards-list">
          @for (card of cards(); track card.cardId) {
            <div class="card-item">
              <div class="card-visual">
                <div class="card-chip"></div>
                <div class="card-type">{{ card.cardType }}</div>
                <div class="card-number">**** **** **** {{ card.cardNumber.slice(-4) }}</div>
              </div>
              <div class="card-details">
                <div class="detail-row">
                  <span class="label">Total Limit:</span>
                  <span class="value">\${{ card.totalLimit | number }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Amount Used:</span>
                  <span class="value used">\${{ card.amountUsed | number }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Available:</span>
                  <span class="value available">\${{ card.availableAmount | number }}</span>
                </div>
                <div class="progress-bar">
                  <div class="progress" [style.width.%]="(card.amountUsed / card.totalLimit) * 100"></div>
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    h1 {
      margin-bottom: 2rem;
      color: #2c3e50;
    }

    .cards-list {
      display: grid;
      gap: 2rem;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    }

    .card-item {
      background: white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .card-visual {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem;
      position: relative;
      min-height: 180px;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
    }

    .card-chip {
      width: 50px;
      height: 40px;
      background: linear-gradient(135deg, #ffd89b 0%, #19547b 100%);
      border-radius: 8px;
      margin-bottom: 1rem;
    }

    .card-type {
      font-size: 0.9rem;
      text-transform: uppercase;
      letter-spacing: 2px;
      opacity: 0.9;
    }

    .card-number {
      font-size: 1.3rem;
      letter-spacing: 4px;
      font-family: 'Courier New', monospace;
      margin-top: 1rem;
    }

    .card-details {
      padding: 1.5rem;
    }

    .detail-row {
      display: flex;
      justify-content: space-between;
      padding: 0.5rem 0;
    }

    .label {
      color: #7f8c8d;
      font-weight: 500;
    }

    .value {
      font-weight: 600;
      color: #2c3e50;
    }

    .value.used {
      color: #e67e22;
    }

    .value.available {
      color: #27ae60;
    }

    .progress-bar {
      margin-top: 1rem;
      height: 8px;
      background: #ecf0f1;
      border-radius: 4px;
      overflow: hidden;
    }

    .progress {
      height: 100%;
      background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
      transition: width 0.3s ease;
    }

    .error-message {
      color: #e74c3c;
      padding: 1rem;
      background: #fadbd8;
      border-radius: 4px;
      margin: 1rem 0;
    }
  `],
})
export class CardsComponent implements OnInit {
  cards = signal<Card[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCards();
  }

  private loadCards(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage.set('User not authenticated');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = new HttpParams().set('id', user.id.toString());

    this.apiService.get<Card[]>(API_CONFIG.endpoints.cards, params).subscribe({
      next: (cards) => {
        this.cards.set(cards);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load cards');
        this.isLoading.set(false);
        console.error('Error loading cards:', error);
      }
    });
  }
}
