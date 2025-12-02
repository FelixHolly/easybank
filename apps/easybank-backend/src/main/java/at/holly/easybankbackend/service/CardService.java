package at.holly.easybankbackend.service;

import at.holly.easybankbackend.dto.CardDto;
import at.holly.easybankbackend.dto.CardSummary;
import at.holly.easybankbackend.dto.PageResponse;
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

import java.math.BigDecimal;
import java.math.RoundingMode;

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
   * Get card details for authenticated user (paginated) with summary metadata
   * Automatically provisions user from Keycloak on first access (JIT provisioning)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters
   * @return page response with card DTOs and card summary
   */
  @Transactional(readOnly = true)
  public PageResponse<CardDto, CardSummary> getCardsWithSummary(
      Authentication authentication, Pageable pageable) {
    log.info("Fetching card details with summary for authenticated user (page {}, size {})",
        pageable.getPageNumber(), pageable.getPageSize());

    // Get or create user (JIT provisioning)
    User user = userProvisioningService.getOrCreateUser(authentication);
    log.info("User found (ID: {})", user.getId());

    // Fetch paginated cards
    Page<Card> cardsPage = cardRepository.findByUserId(user.getId(), pageable);
    log.info("Retrieved {} cards (page {} of {}) for user ID: {}",
        cardsPage.getNumberOfElements(), cardsPage.getNumber() + 1,
        cardsPage.getTotalPages(), user.getId());

    // Compute card summary (aggregates from ALL cards, not just current page)
    BigDecimal totalCreditLimit = cardRepository.sumTotalLimitByUserId(user.getId());
    BigDecimal totalAvailable = cardRepository.sumAvailableByUserId(user.getId());
    BigDecimal totalUsed = cardRepository.sumUsedByUserId(user.getId());
    Long cardCount = cardRepository.countByUserId(user.getId());

    // Calculate overall utilization percentage
    double overallUtilization = 0.0;
    if (totalCreditLimit.compareTo(BigDecimal.ZERO) > 0) {
      overallUtilization = totalUsed
          .divide(totalCreditLimit, 4, RoundingMode.HALF_UP)
          .multiply(BigDecimal.valueOf(100))
          .doubleValue();
    }

    CardSummary summary = CardSummary.builder()
        .totalCreditLimit(totalCreditLimit)
        .totalAvailable(totalAvailable)
        .totalUsed(totalUsed)
        .overallUtilization(overallUtilization)
        .cardCount(cardCount)
        .build();

    log.info("Card summary computed - limit: {}, available: {}, used: {}, utilization: {}%, count: {}",
        totalCreditLimit, totalAvailable, totalUsed, overallUtilization, cardCount);

    // Map to DTOs and wrap in PageResponse
    Page<CardDto> dtoPage = cardsPage.map(cardMapper::toDto);
    return PageResponse.of(dtoPage, summary);
  }
}
