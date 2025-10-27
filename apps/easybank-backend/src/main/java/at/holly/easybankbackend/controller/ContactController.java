package at.holly.easybankbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

  @GetMapping("/contact")
  public String saveContactInquiryDetails () {
    return "Contact inquiry details saved";
  }

}
