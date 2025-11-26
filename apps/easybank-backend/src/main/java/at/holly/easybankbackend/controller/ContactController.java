package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.ContactDto;
import at.holly.easybankbackend.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contact Controller
 * Handles customer support inquiry HTTP endpoints
 * Delegates business logic to ContactService
 */
@RestController
@RequiredArgsConstructor
public class ContactController {

  private final ContactService contactService;

  /**
   * Submit a contact/support inquiry
   * This endpoint is public - no authentication required
   *
   * @param contactDto the contact inquiry details (validated)
   * @return the saved contact inquiry DTO with generated ID and timestamp
   */
  @PostMapping("/contact")
  public ResponseEntity<ContactDto> saveContactInquiryDetails(@Valid @RequestBody ContactDto contactDto) {
    ContactDto savedContact = contactService.saveContactInquiry(contactDto);
    return ResponseEntity.ok(savedContact);
  }

}
