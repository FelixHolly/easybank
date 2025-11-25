import {Component, inject} from '@angular/core';
import {RouterLink} from '@angular/router';
import {AuthService} from '../../features/auth/services/auth.service';

/**
 * Home Component (Public Landing Page)
 * Marketing/welcome page for unauthenticated users
 * Redirects authenticated users to dashboard
 */
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {
  private authService = inject(AuthService);

  /**
   * Trigger Keycloak login
   * User will be redirected to Keycloak's login page
   */
  async onLogin(): Promise<void> {
    await this.authService.login();
  }
}
