import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ApiService, LoggerService} from '../../../core';
import {API_CONFIG} from '../../../config';
import {Loan} from '../../../shared/models/financial.model';
import {NavComponent} from "../../../shared/components/navigation/nav.component";

/**
 * Loans Component
 * Manage and view loan information
 */
@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './loans.component.html',
  styleUrl: './loans.component.scss',
})
export class LoansComponent implements OnInit {
  loans = signal<Loan[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private logger = inject(LoggerService);

  ngOnInit(): void {
    this.loadLoans();
  }

  private loadLoans(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Backend extracts identity from JWT; no email required
    this.apiService.get<Loan[]>(API_CONFIG.endpoints.loans).subscribe({
      next: (loans) => {
        this.loans.set(loans);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load loans. Please try again.');
        this.isLoading.set(false);
        this.logger.error('Error loading loans:', error);
      },
    });
  }

  retry(): void {
    this.loadLoans();
  }

  totalLoanAmount(): number {
    return this.loans().reduce((sum, loan) => sum + (loan.totalLoan || 0), 0);
  }

  totalOutstandingAmount(): number {
    return this.loans().reduce((sum, loan) => sum + (loan.outstandingAmount || 0), 0);
  }

  repaymentPercent(loan: Loan): number {
    if (!loan.totalLoan || loan.totalLoan <= 0) return 0;
    return Math.min(
      100,
      Math.max(0, (loan.amountPaid / loan.totalLoan) * 100)
    );
  }

  isFullyRepaid(loan: Loan): boolean {
    return (loan.outstandingAmount ?? 0) <= 0;
  }

  isEarlyStage(loan: Loan): boolean {
    const pct = this.repaymentPercent(loan);
    return pct < 25;
  }

  isMidStage(loan: Loan): boolean {
    const pct = this.repaymentPercent(loan);
    return pct >= 25 && pct < 75;
  }

  isLateStage(loan: Loan): boolean {
    const pct = this.repaymentPercent(loan);
    return pct >= 75;
  }
}
