import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config/api.config';
import { AccountTransaction } from '../../../shared/models/financial.model';

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
      <h1>Transaction History</h1>

      @if (isLoading()) {
        <p>Loading transactions...</p>
      } @else if (errorMessage()) {
        <div class="error-message">{{ errorMessage() }}</div>
      } @else if (transactions().length === 0) {
        <p>No transactions found.</p>
      } @else {
        <div class="balance-summary">
          <div class="summary-card">
            <h3>Current Balance</h3>
            <div class="balance-amount">\${{ currentBalance() | number }}</div>
          </div>
        </div>

        <div class="transactions-list">
          <h2>Recent Transactions</h2>
          <div class="transaction-table">
            @for (transaction of transactions(); track transaction.transactionId) {
              <div class="transaction-row" [class.credit]="transaction.transactionType === 'Credit'" [class.debit]="transaction.transactionType === 'Debit'">
                <div class="transaction-date">
                  <div class="date">{{ transaction.transactionDt | date: 'MMM d, y' }}</div>
                  <div class="id">{{ transaction.transactionId }}</div>
                </div>
                <div class="transaction-details">
                  <div class="summary">{{ transaction.transactionSummary }}</div>
                  <div class="type">{{ transaction.transactionType }}</div>
                </div>
                <div class="transaction-amount">
                  <div class="amount" [class.positive]="transaction.transactionType === 'Credit'" [class.negative]="transaction.transactionType === 'Debit'">
                    {{ transaction.transactionType === 'Credit' ? '+' : '-' }}\${{ transaction.transactionAmt | number }}
                  </div>
                  <div class="closing-balance">Balance: \${{ transaction.closingBalance | number }}</div>
                </div>
              </div>
            }
          </div>
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

    .balance-summary {
      margin-bottom: 2rem;
    }

    .summary-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem;
      border-radius: 12px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    }

    .summary-card h3 {
      margin: 0 0 1rem 0;
      font-size: 1rem;
      opacity: 0.9;
      text-transform: uppercase;
      letter-spacing: 1px;
    }

    .balance-amount {
      font-size: 2.5rem;
      font-weight: 700;
    }

    .transactions-list h2 {
      margin-bottom: 1.5rem;
      color: #2c3e50;
      font-size: 1.5rem;
    }

    .transaction-table {
      background: white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .transaction-row {
      display: grid;
      grid-template-columns: 150px 1fr 200px;
      gap: 1rem;
      padding: 1.5rem;
      border-bottom: 1px solid #ecf0f1;
      transition: background 0.2s;
    }

    .transaction-row:hover {
      background: #f8f9fa;
    }

    .transaction-row:last-child {
      border-bottom: none;
    }

    .transaction-date .date {
      font-weight: 600;
      color: #2c3e50;
      margin-bottom: 0.25rem;
    }

    .transaction-date .id {
      font-size: 0.8rem;
      color: #7f8c8d;
    }

    .transaction-details .summary {
      font-weight: 500;
      color: #2c3e50;
      margin-bottom: 0.25rem;
    }

    .transaction-details .type {
      font-size: 0.85rem;
      color: #7f8c8d;
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .transaction-amount {
      text-align: right;
    }

    .transaction-amount .amount {
      font-size: 1.2rem;
      font-weight: 700;
      margin-bottom: 0.25rem;
    }

    .transaction-amount .amount.positive {
      color: #27ae60;
    }

    .transaction-amount .amount.negative {
      color: #e74c3c;
    }

    .transaction-amount .closing-balance {
      font-size: 0.85rem;
      color: #7f8c8d;
    }

    .error-message {
      color: #e74c3c;
      padding: 1rem;
      background: #fadbd8;
      border-radius: 4px;
      margin: 1rem 0;
    }

    @media (max-width: 768px) {
      .transaction-row {
        grid-template-columns: 1fr;
        gap: 0.5rem;
      }

      .transaction-amount {
        text-align: left;
      }
    }
  `],
})
export class BalanceComponent implements OnInit {
  transactions = signal<AccountTransaction[]>([]);
  currentBalance = signal(0);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadTransactions();
  }

  private loadTransactions(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage.set('User not authenticated');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = new HttpParams().set('id', user.id.toString());

    this.apiService.get<AccountTransaction[]>(API_CONFIG.endpoints.balance, params).subscribe({
      next: (transactions) => {
        this.transactions.set(transactions);
        // Set current balance from the most recent transaction
        if (transactions.length > 0) {
          this.currentBalance.set(transactions[0].closingBalance);
        }
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load transactions');
        this.isLoading.set(false);
        console.error('Error loading transactions:', error);
      }
    });
  }
}
