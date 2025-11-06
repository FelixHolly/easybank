package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Contact;
import at.holly.easybankbackend.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class ContactController {

  private final ContactRepository contactRepository;

  @PostMapping("/contact")
  public ResponseEntity<Contact> saveContactInquiryDetails (@RequestBody Contact contact) {
    contact.setContactId(generateContactId());
    contact.setCreateDt(new Date(System.currentTimeMillis()));
    Contact savedContact = contactRepository.save(contact);
    return ResponseEntity.ok(savedContact);
  }

  private String generateContactId() {
    return String.format("SR-%06d", new Random().nextInt(1000000));
  }

}
