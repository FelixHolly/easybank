import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavComponent } from '../../../shared/components/navigation/nav.component';
import { LoansStore, LoanWithMetrics } from '../store/loans.store';

/**
 * Loans Component
 * Manage and view loan information
 * Now using NgRx SignalStore for state management
 */
@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './loans.component.html',
  styleUrl: './loans.component.scss',
})
export class LoansComponent implements OnInit {
  private loansStore = inject(LoansStore);

  // Expose store signals
  readonly loans = this.loansStore.loansWithMetrics;
  readonly loading = this.loansStore.loading;
  readonly error = this.loansStore.error;
  readonly totalLoanAmount = this.loansStore.totalLoanAmount;
  readonly totalOutstanding = this.loansStore.totalOutstanding;
  readonly totalPaid = this.loansStore.totalPaid;
  readonly activeLoans = this.loansStore.activeLoans;
  readonly completedLoans = this.loansStore.completedLoans;
  readonly hasLoans = this.loansStore.hasLoans;

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
