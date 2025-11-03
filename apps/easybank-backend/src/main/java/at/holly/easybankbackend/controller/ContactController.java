package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Contact;
import at.holly.easybankbackend.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.Random;

@RestController
@RequiredArgsConstructor
public class ContactController {

  private final ContactRepository contactRepository;

  @GetMapping("/contact")
  public Contact saveContactInquiryDetails (@RequestBody Contact contact) {
    contact.setContactId(generateContactId());
    contact.setCreateDt(new Date(System.currentTimeMillis()));
    return contactRepository.save(contact);
  }

  private String generateContactId() {
    return String.format("SR-%06d", new Random().nextInt(1000000));
  }

}
