package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.CardDto;
import at.holly.easybankbackend.dto.CardMapper;
import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Card Service
 * Handles business logic for card operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {

  private final CardRepository cardRepository;
  private final UserProvisioningService userProvisioningService;
  private final CardMapper cardMapper;

  /**
   * Get card details for authenticated user
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @return list of card DTOs
   */
  @Transactional(readOnly = true)
  public List<CardDto> getCardsForUser(Authentication authentication) {
    log.info("Fetching card details for authenticated user");

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

    // Fetch cards
    List<Card> cards = cardRepository.findByUserId(user.getId());
    log.info("Retrieved {} cards for user: {}", cards.size(), user.getEmail());

    return cardMapper.toDtoList(cards);
  }
}
