import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core';
import { AuthService } from '../../auth/services/auth.service';
import { API_CONFIG } from '../../../config';
import { Card } from '../../../shared/models/financial.model';
import {NavComponent} from "../../../shared/components/navigation/nav.component";

/**
 * Cards Component
 * Manage credit/debit cards
 */
@Component({
  selector: 'app-cards',
  standalone: true,
  imports: [CommonModule, NavComponent],
  templateUrl: './cards.component.html',
  styleUrl: './cards.component.scss',
})
export class CardsComponent implements OnInit {
  cards = signal<Card[]>([]);
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);

  private apiService = inject(ApiService);
  private authService = inject(AuthService); // kept for future behavior if needed

  ngOnInit(): void {
    this.loadCards();
  }

  private loadCards(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    // Backend extracts identity from JWT; no email required
    this.apiService.get<Card[]>(API_CONFIG.endpoints.cards).subscribe({
      next: (cards) => {
        this.cards.set(cards);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.errorMessage.set('Failed to load cards. Please try again.');
        this.isLoading.set(false);
        console.error('Error loading cards:', error);
      },
    });
  }

  retry(): void {
    this.loadCards();
  }

  maskedCardNumber(card: Card): string {
    const num = String(card.cardNumber ?? '');
    if (!num || num.length < 4) {
      return '•••• •••• •••• ••••';
    }
    const last4 = num.slice(-4);
    return `•••• •••• •••• ${last4}`;
  }

  utilizationPercent(card: Card): number {
    if (!card.totalLimit || card.totalLimit <= 0) return 0;
    return Math.min(
      100,
      Math.max(0, (card.amountUsed / card.totalLimit) * 100)
    );
  }

  isHighUtilization(card: Card): boolean {
    return this.utilizationPercent(card) >= 80;
  }

  isLowUtilization(card: Card): boolean {
    const p = this.utilizationPercent(card);
    return p > 0 && p < 30;
  }

  totalCreditLimit(): number {
    return this.cards().reduce((sum, c) => sum + (c.totalLimit || 0), 0);
  }

  totalAvailable(): number {
    return this.cards().reduce((sum, c) => sum + (c.availableAmount || 0), 0);
  }
}
