import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {User} from '../../../core';
import {AuthService} from '../../auth/services/auth.service';
import {NavComponent} from "../../../shared/components/navigation/nav.component";
import {AccountStore} from '../store/account.store';

/**
 * Account Component
 * User account overview and profile details
 * Now using NgRx SignalStore for state management
 */
@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss',
})
export class AccountComponent implements OnInit {
  private authService = inject(AuthService);
  private accountStore = inject(AccountStore);

  // User state
  currentUser = signal<User | null>(null);

  // Account state from store (signals)
  readonly account = this.accountStore.data;
  readonly loading = this.accountStore.loading;
  readonly error = this.accountStore.error;

  ngOnInit(): void {
    this.currentUser.set(this.authService.getCurrentUser());
    this.accountStore.loadAccount();
  }

  retry(): void {
    this.accountStore.retry();
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
