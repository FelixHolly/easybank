package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.ContactDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ContactController validation
 * Verifies that validation annotations work with the GlobalExceptionHandler
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("ContactController Validation Integration Tests")
class ContactControllerValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  @DisplayName("Should accept valid contact request")
  void shouldAcceptValidContactRequest() throws Exception {
    ContactDto validContact = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account settings and password reset")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(validContact)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.contactId").exists())
        .andExpect(jsonPath("$.contactName").value("John Doe"))
        .andExpect(jsonPath("$.contactEmail").value("john.doe@example.com"));
  }

  @Test
  @DisplayName("Should reject request with blank contact name")
  void shouldRejectBlankContactName() throws Exception {
    ContactDto invalidContact = ContactDto.builder()
        .contactName("")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.message").value("Validation failed for one or more fields"))
        .andExpect(jsonPath("$.validationErrors").isArray())
        // Blank triggers both @NotBlank and @Size validations
        .andExpect(jsonPath("$.validationErrors.length()").value(2))
        .andExpect(jsonPath("$.validationErrors[0].field").value("contactName"));
  }

  @Test
  @DisplayName("Should reject request with invalid email format")
  void shouldRejectInvalidEmailFormat() throws Exception {
    ContactDto invalidContact = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("not-an-email")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.validationErrors").isArray())
        .andExpect(jsonPath("$.validationErrors[0].field").value("contactEmail"))
        .andExpect(jsonPath("$.validationErrors[0].message").value("Email must be valid"));
  }

  @Test
  @DisplayName("Should reject request with subject too short")
  void shouldRejectSubjectTooShort() throws Exception {
    ContactDto invalidContact = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Help")
        .message("I need help with my account")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.validationErrors[0].field").value("subject"))
        .andExpect(jsonPath("$.validationErrors[0].message").value("Subject must be between 5 and 200 characters"));
  }

  @Test
  @DisplayName("Should reject request with message too short")
  void shouldRejectMessageTooShort() throws Exception {
    ContactDto invalidContact = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("Too short")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.validationErrors[0].field").value("message"))
        .andExpect(jsonPath("$.validationErrors[0].message").value("Message must be between 10 and 2000 characters"));
  }

  @Test
  @DisplayName("Should return multiple validation errors for multiple invalid fields")
  void shouldReturnMultipleValidationErrors() throws Exception {
    ContactDto invalidContact = ContactDto.builder()
        .contactName("")
        .contactEmail("invalid-email")
        .subject("Hi")
        .message("Short")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.validationErrors").isArray())
        // contactName (blank) = 2 violations, email (invalid) = 1, subject (short) = 1, message (short) = 1
        .andExpect(jsonPath("$.validationErrors.length()").value(5));
  }

  @Test
  @DisplayName("Should include timestamp and path in error response")
  void shouldIncludeTimestampAndPathInErrorResponse() throws Exception {
    ContactDto invalidContact = ContactDto.builder()
        .contactName("")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.timestamp").exists())
        .andExpect(jsonPath("$.path").value("/contact"));
  }

  @Test
  @DisplayName("Should reject request with contact name too long")
  void shouldRejectContactNameTooLong() throws Exception {
    String longName = "a".repeat(101);
    ContactDto invalidContact = ContactDto.builder()
        .contactName(longName)
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account settings")
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.validationErrors[0].field").value("contactName"));
  }

  @Test
  @DisplayName("Should reject request with message too long")
  void shouldRejectMessageTooLong() throws Exception {
    String longMessage = "a".repeat(2001);
    ContactDto invalidContact = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message(longMessage)
        .build();

    mockMvc.perform(post("/contact")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidContact)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.validationErrors[0].field").value("message"));
  }
}
