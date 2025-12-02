import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../../shared/components/navigation/nav.component';
import { PaginationComponent } from '../../../shared/components/pagination/pagination.component';
import { LoansStore, LoanWithMetrics } from '../store/loans.store';

/**
 * Loans Component
 * Manage and view loan information
 * Now using NgRx SignalStore with pagination support
 */
@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, NavComponent, PaginationComponent],
  templateUrl: './loans.component.html',
  styleUrl: './loans.component.scss',
})
export class LoansComponent implements OnInit {
  readonly loansStore = inject(LoansStore);

  // Expose store signals - data
  readonly loans = this.loansStore.loansWithMetrics;
  readonly loading = this.loansStore.loading;
  readonly error = this.loansStore.error;
  readonly totalLoanAmount = this.loansStore.totalLoanAmount;
  readonly totalOutstanding = this.loansStore.totalOutstanding;
  readonly totalPaid = this.loansStore.totalPaid;

  // Expose store signals - pagination
  readonly currentPage = this.loansStore.currentPage;
  readonly totalPages = this.loansStore.totalPages;
  readonly pageNumbers = this.loansStore.pageNumbers;
  readonly hasPreviousPage = this.loansStore.hasPreviousPage;
  readonly hasNextPage = this.loansStore.hasNextPage;
  readonly pageRange = this.loansStore.pageRange;
  readonly pageSize = this.loansStore.pageSize;
  readonly isPaginationNeeded = this.loansStore.isPaginationNeeded;

  ngOnInit(): void {
    this.loansStore.loadLoans();
  }

  retry(): void {
    this.loansStore.retry();
  }

  // Helper methods for template (data is already computed in store)
  repaymentPercent(loan: LoanWithMetrics): number {
    return loan.repaymentPercent;
  }

  isFullyRepaid(loan: LoanWithMetrics): boolean {
    return loan.isFullyRepaid;
  }

  isEarlyStage(loan: LoanWithMetrics): boolean {
    return loan.stage === 'early';
  }

  isMidStage(loan: LoanWithMetrics): boolean {
    return loan.stage === 'mid';
  }

  isLateStage(loan: LoanWithMetrics): boolean {
    return loan.stage === 'late';
  }
}
