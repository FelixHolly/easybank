import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/**
 * Form Utilities
 * Helper functions for form validation and manipulation
 */

/**
 * Custom validator to check if password and confirmPassword match
 */
export function matchPasswordValidator(
  passwordField: string,
  confirmPasswordField: string
): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const password = control.get(passwordField)?.value;
    const confirmPassword = control.get(confirmPasswordField)?.value;

    if (!password || !confirmPassword) {
      return null;
    }

    return password === confirmPassword ? null : { passwordMismatch: true };
  };
}

/**
 * Mark all controls in a form group as touched
 */
export function markFormGroupTouched(formGroup: AbstractControl): void {
  Object.keys((formGroup as any).controls).forEach((key) => {
    const control = (formGroup as any).get(key);
    control.markAsTouched();

    if (control.controls) {
      markFormGroupTouched(control);
    }
  });
}

/**
 * Get all form validation errors
 */
export function getFormValidationErrors(formGroup: AbstractControl): any[] {
  const errors: any[] = [];

  Object.keys((formGroup as any).controls).forEach((key) => {
    const control = (formGroup as any).get(key);
    const controlErrors = control.errors;

    if (controlErrors) {
      Object.keys(controlErrors).forEach((errorKey) => {
        errors.push({
          field: key,
          error: errorKey,
          value: controlErrors[errorKey],
        });
      });
    }
  });

  return errors;
}
