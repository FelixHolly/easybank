package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CardRepository;
import at.holly.easybankbackend.repository.CustomerRepository;
import at.holly.easybankbackend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

/**
 * Cards Controller
 * Handles card-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class CardsController {

  private final CardRepository cardRepository;
  private final CustomerRepository customerRepository;
  private final JwtService jwtService;

  /**
   * Get card details for a customer
   * Extracts email from JWT token claims
   */
  @GetMapping("/myCards")
  public List<Card> getCardsDetails(Authentication authentication) {
    // Extract email from JWT token using JwtService
    String email = jwtService.extractEmail(authentication);

    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found with email: " + email);
    }

    return cardRepository.findByCustomerId(maybeCustomer.get().getId());
  }

}
