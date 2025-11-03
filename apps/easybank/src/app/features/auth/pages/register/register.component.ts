import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { matchPasswordValidator } from '../../../../shared/utils/form.utils';
import { APP_CONSTANTS } from '../../../../config/app.constants';

/**
 * Register Component
 * Handles new user registration
 */
@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent {
  registerForm: FormGroup;
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group(
      {
        name: ['', [Validators.required, Validators.minLength(2)]],
        email: ['', [Validators.required, Validators.email]],
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(APP_CONSTANTS.validation.passwordMinLength),
          ],
        ],
        confirmPassword: ['', [Validators.required]],
        agreeToTerms: [false, [Validators.requiredTrue]],
      },
      {
        validators: matchPasswordValidator('password', 'confirmPassword'),
      }
    );
  }

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const { confirmPassword, agreeToTerms, ...registerData } = this.registerForm.value;

    // TODO: Implement register API call
    // For now, just simulate registration
    setTimeout(() => {
      this.isLoading.set(false);
      this.router.navigate(['/auth/login'], {
        queryParams: { registered: true },
      });
    }, 1500);

    // Uncomment when API is ready:
    // this.authService.register(registerData).subscribe({
    //   next: () => {
    //     this.isLoading.set(false);
    //     this.router.navigate(['/auth/login'], {
    //       queryParams: { registered: true }
    //     });
    //   },
    //   error: (error) => {
    //     this.isLoading.set(false);
    //     this.errorMessage.set(
    //       error.error?.message || 'Registration failed. Please try again.'
    //     );
    //   }
    // });
  }

  /**
   * Toggle password visibility
   */
  togglePasswordVisibility(field: 'password' | 'confirmPassword'): void {
    if (field === 'password') {
      this.showPassword.set(!this.showPassword());
    } else {
      this.showConfirmPassword.set(!this.showConfirmPassword());
    }
  }

  /**
   * Check if field has error
   */
  hasError(field: string, error: string): boolean {
    const control = this.registerForm.get(field);
    return !!(control?.hasError(error) && control?.touched);
  }

  /**
   * Check if form has error
   */
  hasFormError(error: string): boolean {
    return !!(
      this.registerForm.hasError(error) &&
      (this.registerForm.get('confirmPassword')?.touched ||
        this.registerForm.get('password')?.touched)
    );
  }
}
