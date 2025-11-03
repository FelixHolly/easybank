package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.Account;
import at.holly.easybankbackend.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

  private final AccountRepository accountRepository;

  @GetMapping("/myAccount")
  public Account getAccountDetails (@RequestParam long id) {
    return accountRepository.findByCustomerId(id);
  }

}
