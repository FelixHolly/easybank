import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

/**
 * Forgot Password Component
 * Handles password reset requests
 */
@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.scss',
})
export class ForgotPasswordComponent {
  private fb = inject(FormBuilder);

  forgotPasswordForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });
  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);
  emailSent = signal(false);

  /**
   * Handle form submission
   */
  onSubmit(): void {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const email = this.forgotPasswordForm.value.email;

    // TODO: Implement password reset API call
    // Simulate API call
    setTimeout(() => {
      this.isLoading.set(false);
      this.emailSent.set(true);
      this.successMessage.set(
        'If an account exists with this email, you will receive password reset instructions.'
      );
      this.forgotPasswordForm.disable();
    }, 1500);

    // Uncomment when API is ready:
    // this.authService.requestPasswordReset(email).subscribe({
    //   next: () => {
    //     this.isLoading.set(false);
    //     this.emailSent.set(true);
    //     this.successMessage.set(
    //       'If an account exists with this email, you will receive password reset instructions.'
    //     );
    //     this.forgotPasswordForm.disable();
    //   },
    //   error: (error) => {
    //     this.isLoading.set(false);
    //     this.errorMessage.set(
    //       'An error occurred. Please try again later.'
    //     );
    //   }
    // });
  }

  /**
   * Check if field has error
   */
  hasError(field: string, error: string): boolean {
    const control = this.forgotPasswordForm.get(field);
    return !!(control?.hasError(error) && control?.touched);
  }

  /**
   * Reset form to try again
   */
  resetForm(): void {
    this.forgotPasswordForm.reset();
    this.forgotPasswordForm.enable();
    this.emailSent.set(false);
    this.successMessage.set(null);
    this.errorMessage.set(null);
  }
}
