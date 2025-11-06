import {Component, inject, OnInit, signal} from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpParams } from '@angular/common/http';
import { ApiService } from '../../../core';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config';
import { Card } from '../../../shared/models/financial.model';

/**
 * Cards Component
 * Manage credit/debit cards
 */
@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.scss',
})
export class CardsComponent implements OnInit {
  cards = signal<Card[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private authService = inject(AuthService);


  ngOnInit(): void {
    this.loadCards();
  }

  private loadCards(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      this.errorMessage.set('User not authenticated');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const params = new HttpParams().set('id', user.id.toString());

    this.apiService.get<Card[]>(API_CONFIG.endpoints.cards, params).subscribe({
      next: (cards) => {
        this.cards.set(cards);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load cards');
        this.isLoading.set(false);
        console.error('Error loading cards:', error);
      }
    });
  }
}
