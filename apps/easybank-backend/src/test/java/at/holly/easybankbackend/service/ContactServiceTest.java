package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.ContactDto;
import at.holly.easybankbackend.mapper.ContactMapper;
import at.holly.easybankbackend.model.Contact;
import at.holly.easybankbackend.repository.ContactRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ContactService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ContactService Tests")
class ContactServiceTest {

  @Mock
  private ContactRepository contactRepository;

  @Mock
  private ContactMapper contactMapper;

  @InjectMocks
  private ContactService contactService;

  @Test
  @DisplayName("Should save contact inquiry successfully")
  void shouldSaveContactInquiry() {
    // Given
    ContactDto inputDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Contact contact = new Contact();
    contact.setContactName("John Doe");
    contact.setContactEmail("john.doe@example.com");
    contact.setSubject("Support Request");
    contact.setMessage("I need help with my account");

    Contact savedContact = new Contact();
    savedContact.setContactId("SR-123456");
    savedContact.setContactName("John Doe");
    savedContact.setContactEmail("john.doe@example.com");
    savedContact.setSubject("Support Request");
    savedContact.setMessage("I need help with my account");
    savedContact.setCreateDt(new Date(System.currentTimeMillis()));

    ContactDto outputDto = ContactDto.builder()
        .contactId("SR-123456")
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .createDt(savedContact.getCreateDt())
        .build();

    when(contactMapper.toEntity(inputDto)).thenReturn(contact);
    when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);
    when(contactMapper.toDto(savedContact)).thenReturn(outputDto);

    // When
    ContactDto result = contactService.saveContactInquiry(inputDto);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContactId()).isEqualTo("SR-123456");
    assertThat(result.getContactName()).isEqualTo("John Doe");
    assertThat(result.getContactEmail()).isEqualTo("john.doe@example.com");
    assertThat(result.getCreateDt()).isNotNull();

    verify(contactMapper).toEntity(inputDto);
    verify(contactRepository).save(any(Contact.class));
    verify(contactMapper).toDto(savedContact);
  }

  @Test
  @DisplayName("Should generate unique contact ID in correct format")
  void shouldGenerateContactIdInCorrectFormat() {
    // Given
    ContactDto inputDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Contact contact = new Contact();
    when(contactMapper.toEntity(inputDto)).thenReturn(contact);

    ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
    when(contactRepository.save(contactCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
    when(contactMapper.toDto(any())).thenReturn(new ContactDto());

    // When
    contactService.saveContactInquiry(inputDto);

    // Then
    Contact savedContact = contactCaptor.getValue();
    assertThat(savedContact.getContactId()).isNotNull();
    assertThat(savedContact.getContactId()).startsWith("SR-");
    assertThat(savedContact.getContactId()).matches("SR-[A-F0-9]{8}");
  }

  @Test
  @DisplayName("Should set timestamp when saving contact inquiry")
  void shouldSetTimestamp() {
    // Given
    ContactDto inputDto = ContactDto.builder()
        .contactName("John Doe")
        .contactEmail("john.doe@example.com")
        .subject("Support Request")
        .message("I need help with my account")
        .build();

    Contact contact = new Contact();
    when(contactMapper.toEntity(inputDto)).thenReturn(contact);

    ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
    when(contactRepository.save(contactCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
    when(contactMapper.toDto(any())).thenReturn(new ContactDto());

    long beforeTimestamp = System.currentTimeMillis();

    // When
    contactService.saveContactInquiry(inputDto);

    long afterTimestamp = System.currentTimeMillis();

    // Then
    Contact savedContact = contactCaptor.getValue();
    assertThat(savedContact.getCreateDt()).isNotNull();
    assertThat(savedContact.getCreateDt().getTime()).isBetween(beforeTimestamp, afterTimestamp);
  }

  @Test
  @DisplayName("Should preserve all contact details when saving")
  void shouldPreserveContactDetails() {
    // Given
    ContactDto inputDto = ContactDto.builder()
        .contactName("Jane Smith")
        .contactEmail("jane.smith@example.com")
        .subject("Account Issue")
        .message("Cannot access my account dashboard")
        .build();

    Contact contact = new Contact();
    contact.setContactName("Jane Smith");
    contact.setContactEmail("jane.smith@example.com");
    contact.setSubject("Account Issue");
    contact.setMessage("Cannot access my account dashboard");

    when(contactMapper.toEntity(inputDto)).thenReturn(contact);

    ArgumentCaptor<Contact> contactCaptor = ArgumentCaptor.forClass(Contact.class);
    when(contactRepository.save(contactCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));
    when(contactMapper.toDto(any())).thenReturn(new ContactDto());

    // When
    contactService.saveContactInquiry(inputDto);

    // Then
    Contact savedContact = contactCaptor.getValue();
    assertThat(savedContact.getContactName()).isEqualTo("Jane Smith");
    assertThat(savedContact.getContactEmail()).isEqualTo("jane.smith@example.com");
    assertThat(savedContact.getSubject()).isEqualTo("Account Issue");
    assertThat(savedContact.getMessage()).isEqualTo("Cannot access my account dashboard");
  }
}
