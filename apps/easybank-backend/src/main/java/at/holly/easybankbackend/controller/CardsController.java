package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.CardRepository;
import at.holly.easybankbackend.service.UserProvisioningService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
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
  private final UserProvisioningService userProvisioningService;

  /**
   * Get card details for a user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   */
  @GetMapping("/myCards")
  public List<Card> getCardsDetails(Authentication authentication) {
    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);

    return cardRepository.findByUserId(user.getId());
  }

}
