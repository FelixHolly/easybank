package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.CardDto;
import at.holly.easybankbackend.mapper.CardMapper;
import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.model.User;
import at.holly.easybankbackend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
   * Get card details for authenticated user (paginated)
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters
   * @return page of card DTOs
   */
  @Transactional(readOnly = true)
  public Page<CardDto> getCardsForUser(Authentication authentication, Pageable pageable) {
    log.info("Fetching card details for authenticated user (page {}, size {})",
        pageable.getPageNumber(), pageable.getPageSize());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch cards with pagination
    Page<Card> cards = cardRepository.findByUserId(user.getId(), pageable);
    log.info("Retrieved {} cards (page {} of {}) for user ID: {}",
        cards.getNumberOfElements(), cards.getNumber() + 1, cards.getTotalPages(), user.getId());

    return cards.map(cardMapper::toDto);
  }
}
