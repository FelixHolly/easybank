import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config/api.config';
import { Account } from '../../../shared/models/financial.model';

/**
 * Account Component
 * User account management and profile
 */
@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="page-container">
      <h1>My Account</h1>

      @if (isLoading()) {
        <p>Loading account details...</p>
      } @else if (errorMessage()) {
        <div class="error-message">{{ errorMessage() }}</div>
      } @else if (account()) {
        <div class="account-container">
          <div class="account-card">
            <h2>Account Information</h2>
            <div class="account-details">
              <div class="detail-group">
                <label>Account Number</label>
                <div class="value">{{ account()!.accountNumber }}</div>
              </div>
              <div class="detail-group">
                <label>Account Type</label>
                <div class="value account-type">{{ account()!.accountType }}</div>
              </div>
              <div class="detail-group">
                <label>Branch Address</label>
                <div class="value">{{ account()!.branchAddress }}</div>
              </div>
              <div class="detail-group">
                <label>Customer ID</label>
                <div class="value">{{ account()!.customerId }}</div>
              </div>
              <div class="detail-group">
                <label>Account Created</label>
                <div class="value">{{ account()!.createDt | date: 'longDate' }}</div>
              </div>
            </div>
          </div>

          @if (currentUser()) {
            <div class="profile-card">
              <h2>Profile Information</h2>
              <div class="profile-details">
                <div class="detail-group">
                  <label>Name</label>
                  <div class="value">{{ currentUser()!.name }}</div>
                </div>
                <div class="detail-group">
                  <label>Email</label>
                  <div class="value">{{ currentUser()!.email }}</div>
                </div>
                <div class="detail-group">
                  <label>Mobile Number</label>
                  <div class="value">{{ currentUser()!.mobileNumber }}</div>
                </div>
                <div class="detail-group">
                  <label>Role</label>
                  <div class="value role">{{ currentUser()!.role }}</div>
                </div>
              </div>
            </div>
          }
        </div>
      }
    </div>
  `,
  styles: [`
    .page-container {
      padding: 2rem;
      max-width: 1200px;
      margin: 0 auto;
    }

    h1 {
      margin-bottom: 2rem;
      color: #2c3e50;
    }

    .account-container {
      display: grid;
      gap: 2rem;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
    }

    .account-card,
    .profile-card {
      background: white;
      border-radius: 12px;
      padding: 2rem;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      border: 1px solid #e0e0e0;
    }

    .account-card h2,
    .profile-card h2 {
      margin: 0 0 1.5rem 0;
      color: #2c3e50;
      font-size: 1.5rem;
      padding-bottom: 1rem;
      border-bottom: 2px solid #667eea;
    }

    .account-details,
    .profile-details {
      display: flex;
      flex-direction: column;
      gap: 1.5rem;
    }

    .detail-group {
      display: flex;
      flex-direction: column;
      gap: 0.5rem;
    }

    .detail-group label {
      font-size: 0.85rem;
      color: #7f8c8d;
      text-transform: uppercase;
      letter-spacing: 1px;
      font-weight: 600;
    }

    .detail-group .value {
      font-size: 1.1rem;
      color: #2c3e50;
      font-weight: 500;
      padding: 0.75rem;
      background: #f8f9fa;
      border-radius: 6px;
      border-left: 3px solid #667eea;
    }

    .detail-group .value.account-type,
    .detail-group .value.role {
      display: inline-block;
      width: fit-content;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-size: 0.95rem;
      text-transform: uppercase;
      letter-spacing: 1px;
      border: none;
    }

    .error-message {
      color: #e74c3c;
      padding: 1rem;
      background: #fadbd8;
      border-radius: 4px;
      margin: 1rem 0;
    }

    @media (max-width: 768px) {
      .account-container {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class AccountComponent implements OnInit {
  account = signal<Account | null>(null);
  currentUser = signal(this.authService.getCurrentUser());
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  constructor(
    private apiService: ApiService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
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
