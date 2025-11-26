package at.holly.easybankbackend.validation;

import at.holly.easybankbackend.dto.ContactDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ContactDto validation annotations
 */
@DisplayName("ContactDto Validation Tests")
class ContactDtoValidationTest {

  private static Validator validator;

  @BeforeAll
  static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  @DisplayName("Should pass validation with valid contact data")
  void shouldPassValidationWithValidData() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account settings")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).isEmpty();
  }

  @Test
  @DisplayName("Should fail validation when contact name is blank")
  void shouldFailWhenContactNameIsBlank() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    // Blank triggers both @NotBlank and @Size(min=2)
    assertThat(violations).hasSize(2);
    assertThat(violations)
        .anyMatch(v -> v.getMessage().equals("Contact name is required"));
  }

  @Test
  @DisplayName("Should fail validation when contact name is too short")
  void shouldFailWhenContactNameIsTooShort() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("J")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("must be between 2 and 100 characters");
  }

  @Test
  @DisplayName("Should fail validation when contact name is too long")
  void shouldFailWhenContactNameIsTooLong() {
    String longName = "a".repeat(101);
    ContactDto contactDto = ContactDto.builder()
        .contactName(longName)
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("must be between 2 and 100 characters");
  }

  @Test
  @DisplayName("Should fail validation when email is blank")
  void shouldFailWhenEmailIsBlank() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Contact email is required");
  }

  @Test
  @DisplayName("Should fail validation when email format is invalid")
  void shouldFailWhenEmailFormatIsInvalid() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("invalid-email")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .isEqualTo("Email must be valid");
  }

  @Test
  @DisplayName("Should fail validation when email is too long")
  void shouldFailWhenEmailIsTooLong() {
    String longEmail = "a".repeat(95) + "@ex.com"; // 102 characters total
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail(longEmail)
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).isNotEmpty();
    assertThat(violations)
        .anyMatch(v -> v.getMessage().contains("must not exceed 100 characters"));
  }

  @Test
  @DisplayName("Should fail validation when subject is blank")
  void shouldFailWhenSubjectIsBlank() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    // Blank triggers both @NotBlank and @Size(min=5)
    assertThat(violations).hasSize(2);
    assertThat(violations)
        .anyMatch(v -> v.getMessage().equals("Subject is required"));
  }

  @Test
  @DisplayName("Should fail validation when subject is too short")
  void shouldFailWhenSubjectIsTooShort() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Help")
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("must be between 5 and 200 characters");
  }

  @Test
  @DisplayName("Should fail validation when subject is too long")
  void shouldFailWhenSubjectIsTooLong() {
    String longSubject = "a".repeat(201);
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject(longSubject)
        .message("I need help with my account")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("must be between 5 and 200 characters");
  }

  @Test
  @DisplayName("Should fail validation when message is blank")
  void shouldFailWhenMessageIsBlank() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    // Blank triggers both @NotBlank and @Size(min=10)
    assertThat(violations).hasSize(2);
    assertThat(violations)
        .anyMatch(v -> v.getMessage().equals("Message is required"));
  }

  @Test
  @DisplayName("Should fail validation when message is too short")
  void shouldFailWhenMessageIsTooShort() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("Too short")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("must be between 10 and 2000 characters");
  }

  @Test
  @DisplayName("Should fail validation when message is too long")
  void shouldFailWhenMessageIsTooLong() {
    String longMessage = "a".repeat(2001);
    ContactDto contactDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message(longMessage)
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    assertThat(violations).hasSize(1);
    assertThat(violations.iterator().next().getMessage())
        .contains("must be between 10 and 2000 characters");
  }

  @Test
  @DisplayName("Should have multiple violations when multiple fields are invalid")
  void shouldHaveMultipleViolationsWhenMultipleFieldsAreInvalid() {
    ContactDto contactDto = ContactDto.builder()
        .contactName("")
        .contactEmail("invalid-email")
        .subject("Hi")
        .message("Short")
        .build();

    Set<ConstraintViolation<ContactDto>> violations = validator.validate(contactDto);

    // contactName (blank) = 2 violations (@NotBlank + @Size)
    // contactEmail (invalid) = 1 violation (@Email)
    // subject (too short) = 1 violation (@Size)
    // message (too short) = 1 violation (@Size)
    // Total: 5 violations
    assertThat(violations).hasSize(5);
  }
}
