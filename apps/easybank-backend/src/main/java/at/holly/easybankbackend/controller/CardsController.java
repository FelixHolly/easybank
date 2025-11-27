package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.CardDto;
import at.holly.easybankbackend.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
   * Get card details for authenticated user (paginated)
   * Supports query parameters: ?page=0&size=10&sort=cardId,desc
   *
   * @param authentication the authentication object containing JWT token
   * @param pageable pagination and sorting parameters (default: page 0, size 20)
   * @return page of card DTOs
   */
  @GetMapping("/myCards")
  public Page<CardDto> getCardsDetails(
      Authentication authentication,
      @PageableDefault(size = 20) Pageable pageable) {
    return cardService.getCardsForUser(authentication, pageable);
  }

}
