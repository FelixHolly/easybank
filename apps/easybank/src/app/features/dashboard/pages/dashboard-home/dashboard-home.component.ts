import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

/**
 * Dashboard Home Component
 * Main dashboard page after login showing account overview
 */
@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss',
})
export class DashboardHomeComponent implements OnInit {
  accountSummary = {
    balance: 0,
    totalLoans: 0,
    activeCards: 0,
  };

  recentTransactions: any[] = [];

  ngOnInit(): void {
    this.loadDashboardData();
  }

  private loadDashboardData(): void {
    // TODO: Fetch dashboard data from services
    // this.accountService.getSummary().subscribe(...)
  }
}
