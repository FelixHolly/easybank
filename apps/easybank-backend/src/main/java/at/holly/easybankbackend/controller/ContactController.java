package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.ContactDto;
import at.holly.easybankbackend.dto.ContactMapper;
import at.holly.easybankbackend.model.Contact;
import at.holly.easybankbackend.repository.ContactRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Random;

/**
 * Contact Controller
 * Handles customer support inquiries
 */
@RestController
@RequiredArgsConstructor
public class ContactController {

  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;

  /**
   * Submit a contact/support inquiry
   * This endpoint is public - no authentication required
   *
   * @param contactDto the contact inquiry details (validated)
   * @return the saved contact inquiry DTO with generated ID
   */
  @PostMapping("/contact")
  public ResponseEntity<ContactDto> saveContactInquiryDetails(@Valid @RequestBody ContactDto contactDto) {
    // Convert DTO to entity
    Contact contact = contactMapper.toEntity(contactDto);

    // Generate unique contact ID and timestamp
    contact.setContactId(generateContactId());
    contact.setCreateDt(new Date(System.currentTimeMillis()));

    // Save and return as DTO
    Contact savedContact = contactRepository.save(contact);
    return ResponseEntity.ok(contactMapper.toDto(savedContact));
  }

  /**
   * Generate a unique contact ID in format SR-XXXXXX
   *
   * @return the generated contact ID
   */
  private String generateContactId() {
    return String.format("SR-%06d", new Random().nextInt(1000000));
  }

}
