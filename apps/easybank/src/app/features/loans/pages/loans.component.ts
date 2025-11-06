import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config/api.config';
import { Loan } from '../../../shared/models/financial.model';

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

      @if (isLoading()) {
        <p>Loading loans...</p>
      } @else if (errorMessage()) {
        <div class="error-message">{{ errorMessage() }}</div>
      } @else if (loans().length === 0) {
        <p>You have no loans at this time.</p>
      } @else {
        <div class="loans-list">
          @for (loan of loans(); track loan.loanNumber) {
            <div class="loan-card">
              <div class="loan-header">
                <h3>{{ loan.loanType }}</h3>
                <span class="loan-number">Loan #{{ loan.loanNumber }}</span>
              </div>
              <div class="loan-details">
                <div class="detail-row">
                  <span class="label">Total Loan:</span>
                  <span class="value">\${{ loan.totalLoan | number }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Amount Paid:</span>
                  <span class="value paid">\${{ loan.amountPaid | number }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Outstanding Amount:</span>
                  <span class="value outstanding">\${{ loan.outstandingAmount | number }}</span>
                </div>
                <div class="detail-row">
                  <span class="label">Start Date:</span>
                  <span class="value">{{ loan.startDt | date }}</span>
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

    .loans-list {
      display: grid;
      gap: 1.5rem;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    }

    .loan-card {
      background: white;
      border-radius: 8px;
      padding: 1.5rem;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      border: 1px solid #e0e0e0;
    }

    .loan-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 1rem;
      padding-bottom: 1rem;
      border-bottom: 2px solid #f0f0f0;
    }

    .loan-header h3 {
      margin: 0;
      color: #2c3e50;
      font-size: 1.2rem;
    }

    .loan-number {
      color: #7f8c8d;
      font-size: 0.9rem;
    }

    .loan-details {
      display: flex;
      flex-direction: column;
      gap: 0.75rem;
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

    .value.paid {
      color: #27ae60;
    }

    .value.outstanding {
      color: #e74c3c;
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
export class LoansComponent implements OnInit {
  loans = signal<Loan[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadLoans();
  }

  private loadLoans(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage.set('User not authenticated');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = new HttpParams().set('id', user.id.toString());

    this.apiService.get<Loan[]>(API_CONFIG.endpoints.loans, params).subscribe({
      next: (loans) => {
        this.loans.set(loans);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load loans');
        this.isLoading.set(false);
        console.error('Error loading loans:', error);
      }
    });
  }
}
