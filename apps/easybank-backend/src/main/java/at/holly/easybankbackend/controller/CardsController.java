package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Cards Controller
 * Handles card-related operations with authority-based access control
 */
@RestController
@RequiredArgsConstructor
public class CardsController {

  private final CardRepository cardRepository;

  /**
   * Get card details for a customer
   * Requires CARD:READ authority
   */
  @GetMapping("/myCards")
  @PreAuthorize("hasAuthority('CARD:READ')")
  public List<Card> getCardsDetails (@RequestParam long id) {
    return cardRepository.findByCustomerId(id);
  }

}
