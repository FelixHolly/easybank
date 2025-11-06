package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;

@RestController
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;


//  @GetMapping("/csrf")
//  public Map<String, String> csrf(org.springframework.security.web.csrf.CsrfToken token) {
//    return Map.of("token", token.getToken());
//  }

  //todo use DTO instead of Customer
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody Customer customer) {
    try {
      String hashPassword = passwordEncoder.encode(customer.getPassword());
      customer.setPassword(hashPassword);
      customer.setCreateDt(new Date(System.currentTimeMillis()));
      Customer savedCustomer = customerRepository.save(customer);

      if (savedCustomer.getId() > 0){
        return ResponseEntity.ok("User registered successfully");
      }
      return ResponseEntity.badRequest().body("User registration failed");

    } catch (Exception e) {
      return ResponseEntity.badRequest().body("User registration failed");
    }
  }

  @RequestMapping("/user")
  public Customer getUserDetails(Authentication authentication) {
    return customerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return ResponseEntity.ok("Logged out successfully");
  }
}
