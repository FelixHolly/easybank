import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../../shared/components/navigation/nav.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { CardsStore, CardWithMetrics } from '../store/cards.store';

/**
 * Cards Component
 * Manage credit/debit cards
 * Now using NgRx SignalStore with pagination support
 */
@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule, NavComponent, PaginationComponent],
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.scss',
})
export class CardsComponent implements OnInit {
  readonly cardsStore = inject(CardsStore);

  // Expose store signals - data
  readonly cards = this.cardsStore.cardsWithMetrics;
  readonly loading = this.cardsStore.loading;
  readonly error = this.cardsStore.error;
  readonly totalCreditLimit = this.cardsStore.totalCreditLimit;
  readonly totalAvailable = this.cardsStore.totalAvailable;
  readonly totalUsed = this.cardsStore.totalUsed;
  readonly overallUtilization = this.cardsStore.overallUtilization;
  readonly hasCards = this.cardsStore.hasCards;

  // Expose store signals - pagination
  readonly currentPage = this.cardsStore.currentPage;
  readonly totalPages = this.cardsStore.totalPages;
  readonly pageNumbers = this.cardsStore.pageNumbers;
  readonly hasPreviousPage = this.cardsStore.hasPreviousPage;
  readonly hasNextPage = this.cardsStore.hasNextPage;
  readonly pageRange = this.cardsStore.pageRange;
  readonly pageSize = this.cardsStore.pageSize;
  readonly isPaginationNeeded = this.cardsStore.isPaginationNeeded;

  ngOnInit(): void {
    this.cardsStore.loadCards();
  }

  retry(): void {
    this.cardsStore.retry();
  }

  // Helper methods for template (data is already computed in store)
  maskedCardNumber(card: CardWithMetrics): string {
    return card.maskedNumber;
  }

  utilizationPercent(card: CardWithMetrics): number {
    return card.utilizationPercent;
  }

  isHighUtilization(card: CardWithMetrics): boolean {
    return card.isHighUtilization;
  }

  isLowUtilization(card: CardWithMetrics): boolean {
    return card.isLowUtilization;
  }
}
