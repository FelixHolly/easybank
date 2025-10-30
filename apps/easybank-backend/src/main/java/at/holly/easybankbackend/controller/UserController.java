package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;

  //todo use DTO instead of Customer
  @PostMapping("/register")
  ResponseEntity<String> registerUser(@RequestBody Customer customer) {
    try {
      String hashPassword = passwordEncoder.encode(customer.getPassword());
      customer.setPassword(hashPassword);
      Customer savedCustomer = customerRepository.save(customer);

      if (savedCustomer.getId() > 0){
        return ResponseEntity.ok("User registered successfully");
      }
      return ResponseEntity.badRequest().body("User registration failed");

    } catch (Exception e) {
      return ResponseEntity.badRequest().body("User registration failed");
    }
  }
}
