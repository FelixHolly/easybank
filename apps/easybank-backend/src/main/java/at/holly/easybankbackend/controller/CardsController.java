package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Card;
import at.holly.easybankbackend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardsController {

  private final CardRepository cardRepository;

  @GetMapping("/myCards")
  public List<Card> getCardsDetails (@RequestParam long id) {
    return cardRepository.findByCustomerId(id);
  }

}
