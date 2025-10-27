package at.holly.easybankbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardsController {

  @GetMapping("/myCards")
  public String getCardsDetails () {
    return "Cards details";
  }

}
