package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.CardDto;
import at.holly.easybankbackend.dto.CardSummary;
import at.holly.easybankbackend.dto.PageResponse;
import at.holly.easybankbackend.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Cards Controller
 * Handles card-related HTTP endpoints
 * Delegates business logic to CardService
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CardsController {

  private final CardService cardService;

  /**
   * Get card details for authenticated user (paginated) with card summary
   * Supports query parameters: ?page=0&size=10&sort=cardId,desc
   * Returns PageResponse with:
   * - page: Spring Data Page with card content and pagination metadata
   * - metadata: CardSummary with total credit limit, total used, utilization percentage (computed from ALL cards)
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters (default: page 0, size 20)
   * @return page response with card DTOs and card summary
   */
  @GetMapping("/myCards")
  public PageResponse<CardDto, CardSummary> getCardsDetails(
      Authentication authentication,
      @PageableDefault(size = 20) Pageable pageable) {
    return cardService.getCardsWithSummary(authentication, pageable);
  }

}
