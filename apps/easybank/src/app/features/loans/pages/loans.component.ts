import {Component, inject, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config';
import { Loan } from '../../../shared/models/financial.model';

/**
 * Loans Component
 * Manage and view loan information
 */
@Component({
  selector: 'app-loans',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './loans.component.html',
  styleUrl: './loans.component.scss',
})
export class LoansComponent implements OnInit {
  loans = signal<Loan[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private authService = inject(AuthService);

  ngOnInit(): void {
    this.loadLoans();
  }

  private loadLoans(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage.set('User not authenticated');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = new HttpParams().set('id', user.id.toString());

    this.apiService.get<Loan[]>(API_CONFIG.endpoints.loans, params).subscribe({
      next: (loans) => {
        this.loans.set(loans);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load loans');
        this.isLoading.set(false);
        console.error('Error loading loans:', error);
      }
    });
  }
}
