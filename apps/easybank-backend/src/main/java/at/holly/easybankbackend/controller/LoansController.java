package at.holly.easybankbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoansController {

  @GetMapping("/myLoans")
  public String getLoansDetails () {
    return "Loans details";
  }

}
