package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.dto.CardDto;
import at.holly.easybankbackend.service.CardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Cards Controller
 * Handles card-related HTTP endpoints
 * Delegates business logic to CardService
 */
@RestController
@RequiredArgsConstructor
public class CardsController {

  private final CardService cardService;

  /**
   * Get card details for authenticated user
   *
   * @param authentication the authentication object containing JWT token
   * @return list of card DTOs
   */
  @GetMapping("/myCards")
  public List<CardDto> getCardsDetails(Authentication authentication) {
    return cardService.getCardsForUser(authentication);
  }

}
