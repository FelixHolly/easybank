import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../../core';
import { API_CONFIG } from '../../../config';
import { ContactRequestDto, ContactResponseDto } from '../models/contact.model';

/**
 * Contact Component
 * Contact form for customer inquiries and support
 */
@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss',
})
export class ContactComponent {
  private fb = inject(FormBuilder);
  private apiService = inject(ApiService);

  contactForm: FormGroup = this.fb.group({
    contactName: ['', [Validators.required]],
    contactEmail: ['', [Validators.required, Validators.email]],
    subject: ['', [Validators.required]],
    message: ['', [Validators.required, Validators.minLength(10)]],
  });

  isLoading = signal(false);
  errorMessage = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  onSubmit(): void {
    if (this.contactForm.invalid) {
      this.contactForm.markAllAsTouched();
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set(null);

    const contactRequest: ContactRequestDto = this.contactForm.value;

    this.apiService
      .post<ContactResponseDto>(API_CONFIG.endpoints.contact, contactRequest)
      .subscribe({
        next: (response) => {
          this.isLoading.set(false);
          this.successMessage.set(
            `Your inquiry has been submitted with reference ID: ${response.contactId}`
          );
          this.contactForm.reset();
        },
        error: (error) => {
          this.isLoading.set(false);
          this.errorMessage.set('Failed to submit your message. Please try again later.');
          console.error('Error submitting contact form:', error);
        },
      });
  }

  resetForm(): void {
    this.successMessage.set(null);
    this.contactForm.reset();
  }

  hasError(field: string, error: string): boolean {
    const control = this.contactForm.get(field);
    return !!(control?.hasError(error) && control?.touched);
  }
}
