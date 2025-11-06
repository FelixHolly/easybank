import {Component, inject, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config';
import { AccountTransaction } from '../../../shared/models/financial.model';

/**
 * Balance Component
 * View account balance and transaction history
 */
@Component({
  selector: 'app-balance',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.scss',
})
export class BalanceComponent implements OnInit {
  transactions = signal<AccountTransaction[]>([]);
  currentBalance = signal(0);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private authService = inject(AuthService);

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
