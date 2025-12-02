import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../../shared/components/navigation/nav.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { BalanceStore } from '../store/balance.store';
import { AccountTransaction } from '../../../shared/models/financial.model';

/**
 * Balance Component
 * View account balance and transaction history
 * Now using NgRx SignalStore with pagination support
 */
@Component({
  selector: 'app-balance',
  standalone: true,
  imports: [CommonModule, NavComponent, PaginationComponent],
  templateUrl: './balance.component.html',
  styleUrl: './balance.component.scss',
})
export class BalanceComponent implements OnInit {
  readonly balanceStore = inject(BalanceStore);

  // Expose store signals - data (transactions already sorted by backend)
  readonly transactions = this.balanceStore.data;
  readonly currentBalance = this.balanceStore.currentBalance;
  readonly totalCredits = this.balanceStore.totalCredits;
  readonly totalDebits = this.balanceStore.totalDebits;
  readonly loading = this.balanceStore.loading;
  readonly error = this.balanceStore.error;

  // Expose store signals - pagination
  readonly currentPage = this.balanceStore.currentPage;
  readonly totalPages = this.balanceStore.totalPages;
  readonly pageNumbers = this.balanceStore.pageNumbers;
  readonly hasPreviousPage = this.balanceStore.hasPreviousPage;
  readonly hasNextPage = this.balanceStore.hasNextPage;
  readonly pageRange = this.balanceStore.pageRange;
  readonly pageSize = this.balanceStore.pageSize;
  readonly isPaginationNeeded = this.balanceStore.isPaginationNeeded;

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
