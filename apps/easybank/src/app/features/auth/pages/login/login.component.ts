import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { LoginCredentials } from '../../../../core/models/user.model';

/**
 * Login Component
 * Handles user authentication
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  showPassword = signal(false);

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const credentials: LoginCredentials = this.loginForm.value;

    this.authService.login(credentials).subscribe({
      next: () => {
        this.isLoading.set(false);
        this.router.navigate(['/dashboard']);
      },
      error: (error) => {
        this.isLoading.set(false);
        this.errorMessage.set(
          error.error?.message || 'Login failed. Please check your credentials.'
        );
      },
    });
  }

  /**
   * Toggle password visibility
   */
  togglePasswordVisibility(): void {
    this.showPassword.set(!this.showPassword());
  }

  /**
   * Check if field has error
   */
  hasError(field: string, error: string): boolean {
    const control = this.loginForm.get(field);
    return !!(control?.hasError(error) && control?.touched);
  }
}
