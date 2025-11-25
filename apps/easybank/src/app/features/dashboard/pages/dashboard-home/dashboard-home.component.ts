import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import {NavComponent} from "../../../../shared/components/navigation/nav.component";

interface DashboardSummary {
  balance: number;
  totalLoans: number;
  activeCards: number;
}

interface DashboardTransaction {
  id: string | number;
  date: string | Date;
  summary: string;
  type: 'Credit' | 'Debit';
  amount: number;
}

/**
 * Dashboard Home Component
 * Main dashboard page after login showing account overview
 */
@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink, NavComponent],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss',
})
export class DashboardHomeComponent implements OnInit {
  private authService = inject(AuthService);

  summary = signal<DashboardSummary>({
    balance: 0,
    totalLoans: 0,
    activeCards: 0,
  });

  recentTransactions = signal<DashboardTransaction[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    // For now, mock some data so the dashboard looks alive.
    // Replace this with real API/service calls later.
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Simulate data load; plug in your services here.
    setTimeout(() => {
      this.summary.set({
        balance: 4280.35,
        totalLoans: 1,
        activeCards: 3,
      });

      this.recentTransactions.set([
        {
          id: 'TX-1001',
          date: new Date(),
          summary: 'Grocery Store',
          type: 'Debit',
          amount: 74.19,
        },
        {
          id: 'TX-1000',
          date: new Date(new Date().setDate(new Date().getDate() - 1)),
          summary: 'Salary',
          type: 'Credit',
          amount: 2100,
        },
        {
          id: 'TX-0999',
          date: new Date(new Date().setDate(new Date().getDate() - 2)),
          summary: 'Online Subscription',
          type: 'Debit',
          amount: 12.99,
        },
      ]);

      this.isLoading.set(false);
    }, 300);

    // Example shape when using an API:
    // this.dashboardService.getOverview().subscribe({
    //   next: (data) => {
    //     this.summary.set(data.summary);
    //     this.recentTransactions.set(data.recentTransactions);
    //     this.isLoading.set(false);
    //   },
    //   error: (err) => {
    //     this.errorMessage.set('Failed to load dashboard overview. Please try again.');
    //     this.isLoading.set(false);
    //     console.error('Dashboard load error:', err);
    //   }
    // });
  }

  isCredit(tx: DashboardTransaction): boolean {
    return tx.type === 'Credit';
  }

  isDebit(tx: DashboardTransaction): boolean {
    return tx.type === 'Debit';
  }
}
