import {Component, inject, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService, User } from '../../../core';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config';
import { Account } from '../../../shared/models/financial.model';

/**
 * Account Component
 * User account management and profile
 */
@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule],
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
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage.set('User not authenticated');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = new HttpParams().set('id', user.id.toString());

    this.apiService.get<Account>(API_CONFIG.endpoints.account, params).subscribe({
      next: (account) => {
        this.account.set(account);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load account details');
        this.isLoading.set(false);
        console.error('Error loading account:', error);
      }
    });
  }
}
