package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.model.Customer;
import at.holly.easybankbackend.repository.CardRepository;
import at.holly.easybankbackend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  /**
   * Get card details for a customer
   * Requires CARD:READ authority
   */
  @GetMapping("/myCards")
  public List<Card> getCardsDetails (@RequestParam String email) {
    Optional<Customer> maybeCustomer = customerRepository.findByEmail(email);
    if (maybeCustomer.isEmpty()) {
      throw new RuntimeException("Customer not found");
    }

    return cardRepository.findByCustomerId(maybeCustomer.get().getId());
  }

}
