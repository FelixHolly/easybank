package at.holly.easybankbackend.controller;

import at.holly.easybankbackend.model.AccountTransaction;
import at.holly.easybankbackend.repository.AccountTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BalanceController {

  private final AccountTransactionRepository accountTransactionRepository;

  @GetMapping("/myBalance")
  public List<AccountTransaction> getBalanceDetails (@RequestParam long id) {
    return accountTransactionRepository.findByCustomerIdOrderByTransactionDtDesc(id);
  }

}
