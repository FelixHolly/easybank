import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import {AuthService} from "../../../features/auth/services/auth.service";

/**
 * Global Navigation Component
 * Reuses the same design as the /dashboard navbar,
 * with a fixed, responsive hamburger menu.
 */
@Component({
  selector: 'app-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './nav.component.html',
  styleUrl: './nav.component.scss',
})
export class NavComponent {
  private authService = inject(AuthService);

  isMenuOpen = signal(false);

  toggleMenu(): void {
    this.isMenuOpen.update((v) => !v);
  }

  closeMenu(): void {
    this.isMenuOpen.set(false);
  }

  async onLogout(): Promise<void> {
    this.closeMenu();
    await this.authService.logout();
  }
}
