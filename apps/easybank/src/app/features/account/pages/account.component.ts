import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, User } from '../../../core';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config';
import { Account } from '../../../shared/models/financial.model';
import {NavComponent} from "../../../shared/components/navigation/nav.component";

/**
 * Account Component
 * User account overview and profile details
 */
@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss',
})
export class AccountComponent implements OnInit {
  account = signal<Account | null>(null);
  currentUser = signal<User | null>(null);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.currentUser.set(this.authService.getCurrentUser());
    this.loadAccount();
  }

  private loadAccount(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Backend reads identity from JWT; no email required here
    this.apiService.get<Account>(API_CONFIG.endpoints.account).subscribe({
      next: (account) => {
        this.account.set(account);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load account details. Please try again.');
        this.isLoading.set(false);
        console.error('Error loading account:', error);
      },
    });
  }

  retry(): void {
    this.loadAccount();
  }

  getMaskedAccountNumber(): string {
    const acc = this.account();
    if (!acc || !acc.accountNumber) {
      return '—';
    }

    const num = String(acc.accountNumber);
    // Replace all but last 4 digits with •
    return num.replace(/\d(?=\d{4})/g, '•');
  }

  getAccountTypeLabel(): string {
    const type = this.account()?.accountType ?? '';
    if (!type) return 'Account';

    switch (type.toLowerCase()) {
      case 'savings':
        return 'Savings account';
      case 'current':
      case 'checking':
        return 'Current account';
      default:
        return type;
    }
  }

  getUserInitials(): string {
    const user = this.currentUser();
    if (!user?.name) return 'EB';
    const parts = user.name.trim().split(/\s+/);
    const first = parts[0]?.[0] ?? '';
    const last = parts[1]?.[0] ?? '';
    return (first + last).toUpperCase();
  }
}
