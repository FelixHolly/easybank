package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.CustomerDto;
import at.holly.easybankbackend.dto.CustomerMapper;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CustomerRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Customer Controller
 * Handles customer registration, authentication, and profile operations
 * Uses DTOs to avoid exposing entity details
 */
@RestController
@RequiredArgsConstructor
public class CustomerController {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

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
