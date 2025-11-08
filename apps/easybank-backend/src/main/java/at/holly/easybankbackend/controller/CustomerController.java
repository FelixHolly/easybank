package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.CustomerMapper;
import at.holly.easybankbackend.dto.CustomerDto;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.model.Role;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.service.AuthorityService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Customer Controller
 * Handles customer registration, authentication, and profile operations
 * Uses DTOs to avoid exposing entity details
 */
@RestController
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthorityService authorityService;
  private final CustomerMapper customerMapper;

  /**
   * Register a new customer
   * Uses DTO to validate input and prevent over-posting
   */
  @PostMapping("/register")
  public ResponseEntity<String> registerUser(@RequestBody CustomerDto customerDto) {
    try {
      // Check if user already exists
      if (customerRepository.findByEmail(customerDto.getEmail()).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already registered");
      }

      // Convert DTO to entity
      Customer customer = customerMapper.toEntity(customerDto);

      // Hash password
      String hashPassword = passwordEncoder.encode(customer.getPassword());
      customer.setPassword(hashPassword);

      // Set default role
      customer.setRoles(Set.of(Role.USER));

      // Automatically assign authorities based on roles
      customer.setAuthorities(authorityService.getDefaultAuthoritiesForRoles(customer.getRoles()));

      // Save customer
      Customer savedCustomer = customerRepository.save(customer);

      if (savedCustomer.getId() > 0){
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
      }
      return ResponseEntity.badRequest().body("User registration failed");

    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("User registration failed: " + e.getMessage());
    }
  }

  /**
   * Get current user details
   * Returns DTO without sensitive information like password
   */
  @RequestMapping("/user")
  public CustomerDto getUserDetails(Authentication authentication) {
    Customer customer = customerRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RuntimeException("User not found"));
    return customerMapper.toDto(customer);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    if (authentication != null) {
      new SecurityContextLogoutHandler().logout(request, response, authentication);
    }
    return ResponseEntity.ok("Logged out successfully");
  }
}
