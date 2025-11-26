package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.ContactDto;
import at.holly.easybankbackend.dto.ContactMapper;
import at.holly.easybankbackend.model.Contact;
import at.holly.easybankbackend.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.Random;

/**
 * Contact Service
 * Handles business logic for customer support inquiries
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContactService {

  private final ContactRepository contactRepository;
  private final ContactMapper contactMapper;
  private final Random random = new Random();

  /**
   * Submit a contact/support inquiry
   *
   * @param contactDto the contact inquiry details
   * @return the saved contact inquiry DTO with generated ID and timestamp
   */
  @Transactional
  public ContactDto saveContactInquiry(ContactDto contactDto) {
    log.info("Saving contact inquiry from: {}", contactDto.getContactEmail());

    // Convert DTO to entity
    Contact contact = contactMapper.toEntity(contactDto);

    // Generate unique contact ID and timestamp
    contact.setContactId(generateContactId());
    contact.setCreateDt(new Date(System.currentTimeMillis()));

    // Save to database
    Contact savedContact = contactRepository.save(contact);
    log.info("Contact inquiry saved successfully with ID: {}", savedContact.getContactId());

    return contactMapper.toDto(savedContact);
  }

  /**
   * Generate a unique contact ID in format SR-XXXXXX
   * SR = Support Request
   *
   * @return the generated contact ID
   */
  private String generateContactId() {
    return String.format("SR-%06d", random.nextInt(1000000));
  }
}
