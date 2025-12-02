import {Component, computed, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterLink} from '@angular/router';
import {NavComponent} from "../../../../shared/components/navigation/nav.component";
import {BalanceStore} from '../../../balance/store/balance.store';
import {LoansStore} from '../../../loans/store/loans.store';
import {CardsStore} from '../../../cards/store/cards.store';

/**
 * Dashboard Home Component
 * Main dashboard page after login showing account overview
 * Now uses real data from NgRx SignalStores
 */
@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink, NavComponent],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss',
})
export class DashboardHomeComponent implements OnInit {
  readonly balanceStore = inject(BalanceStore);
  readonly loansStore = inject(LoansStore);
  readonly cardsStore = inject(CardsStore);

  // Computed summary from stores
  readonly summary = computed(() => ({
    balance: this.balanceStore.currentBalance(),
    totalLoans: this.loansStore.activeLoanCount(),
    activeCards: this.cardsStore.cardCount(),
  }));

  // Recent transactions (first 5) from balance store
  readonly recentTransactions = computed(() => {
    const transactions = this.balanceStore.data();
    return transactions ? transactions.slice(0, 5) : [];
  });

  // Aggregate loading state from all stores
  readonly isLoading = computed(() =>
    this.balanceStore.loading() ||
    this.loansStore.loading() ||
    this.cardsStore.loading()
  );

  // Aggregate error state from all stores
  readonly errorMessage = computed(() =>
    this.balanceStore.error() ||
    this.loansStore.error() ||
    this.cardsStore.error()
  );

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    // Load data from all stores
    this.balanceStore.loadTransactions();
    this.loansStore.loadLoans();
    this.cardsStore.loadCards();
  }

  isCredit(tx: { transactionType: string }): boolean {
    return tx.transactionType === 'Credit';
  }

  isDebit(tx: { transactionType: string }): boolean {
    return tx.transactionType === 'Debit';
  }
}
