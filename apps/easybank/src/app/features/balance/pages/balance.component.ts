import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ApiService, LoggerService} from '../../../core';
import {API_CONFIG} from '../../../config';
import {AccountTransaction} from '../../../shared/models/financial.model';
import {NavComponent} from "../../../shared/components/navigation/nav.component";

/**
 * Balance Component
 * View account balance and transaction history
 */
@Component({
  selector: 'app-balance',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.scss',
})
export class BalanceComponent implements OnInit {
  transactions = signal<AccountTransaction[]>([]);
  currentBalance = signal(0);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private logger = inject(LoggerService);

  ngOnInit(): void {
    this.loadTransactions();
  }

  private loadTransactions(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Backend extracts identity from JWT; no email needed here
    this.apiService
      .get<AccountTransaction[]>(API_CONFIG.endpoints.balance)
      .subscribe({
        next: (transactions) => {
          this.transactions.set(transactions);
          if (transactions.length > 0) {
            // Assuming first is most recent
            this.currentBalance.set(transactions[0].closingBalance);
          }
          this.isLoading.set(false);
        },
        error: (error) => {
          this.errorMessage.set('Failed to load transactions. Please try again.');
          this.isLoading.set(false);
          this.logger.error('Error loading transactions:', error);
        },
      });
  }

  retry(): void {
    this.loadTransactions();
  }

  isCredit(tx: AccountTransaction): boolean {
    return tx.transactionType === 'Credit';
  }

  isDebit(tx: AccountTransaction): boolean {
    return tx.transactionType === 'Debit';
  }
}
