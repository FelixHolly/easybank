/**
 * Contact Models and DTOs
 * Used for contact form submissions
 */

/**
 * DTO for sending contact request to backend
 */
export interface ContactRequestDto {
  contactName: string;
  contactEmail: string;
  subject: string;
  message: string;
}

/**
 * DTO for receiving contact response from backend
 */
export interface ContactResponseDto {
  contactId: string;
  contactName: string;
  contactEmail: string;
  subject: string;
  message: string;
  createDt: string;
}
