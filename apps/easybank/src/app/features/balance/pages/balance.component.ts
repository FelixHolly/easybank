import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../../shared/components/navigation/nav.component';
import { BalanceStore } from '../store/balance.store';
import { AccountTransaction } from '../../../shared/models/financial.model';

/**
 * Balance Component
 * View account balance and transaction history
 * Now using NgRx SignalStore for state management
 */
@Component({
  selector: 'app-balance',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.scss',
})
export class BalanceComponent implements OnInit {
  private balanceStore = inject(BalanceStore);

  // Expose store signals
  readonly transactions = this.balanceStore.sortedTransactions;
  readonly currentBalance = this.balanceStore.currentBalance;
  readonly totalCredits = this.balanceStore.totalCredits;
  readonly totalDebits = this.balanceStore.totalDebits;
  readonly loading = this.balanceStore.loading;
  readonly error = this.balanceStore.error;
  readonly hasTransactions = this.balanceStore.hasTransactions;

  ngOnInit(): void {
    this.balanceStore.loadTransactions();
  }

  retry(): void {
    this.balanceStore.retry();
  }

  isCredit(tx: AccountTransaction): boolean {
    return this.balanceStore.isCredit(tx);
  }

  isDebit(tx: AccountTransaction): boolean {
    return this.balanceStore.isDebit(tx);
  }
}
